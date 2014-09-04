# ############################################
#
# Edward Robinson
#
# Python script to sync the device db
# and the phone db. Sync via xml messages.
#
# ############################################

from __future__ import division

# import os
# import signal
# import time
import MySQLdb
import logging
import datetime
import sys
import cloop_config
import pump_interface
import cloop_db
# from bluetooth import *
# from time import sleep
if "linux" in sys.platform:
    windowsConfig = False
else:
    windowsConfig = True
# use ISO format
dateFormat = "%Y-%m-%dT%H:%M:%S"
mySQLDateFormat = "%Y-%m-%d %H:%M:%S"

now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)

if windowsConfig:
    # windows config
    logging.basicConfig(filename=currentDate + '-injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # device config
    logging.basicConfig(filename="./log/" + currentDate + '-injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class Injection():
    injection_id = None
    injection_type = None
    cur_iob_units = None
    cur_bg_units = None
    cur_bg = None
    carbs_to_cover = None
    carbs_units = None
    cur_basal_units = None
    all_meal_carbs_absorbed = None
    correction_units = None
    injection_units = None
    temp_rate = None
    attempted = False
    successfully_executed = False


class InjectionProcess():
    cloop_db = cloop_db.CloopDB()
    cloop_config = cloop_config.CloopConfig()

    def process_injection(self):
        automode = self.get_automode()
        # get injection aka do calculation
        inj = self.injection_calc()
        # no injection needed
        if inj is None:
            return
        # execute injection
        inj = self.do_injection(inj, automode)
        # add alerts
        self.add_inj_alert_and_log(inj)

    # return the amount insulin that should be injected
    # return null if no injection needed
    # based on carbs, IOB, current BG (if CGM accurate), carb sensitivity, bg sensitivity
    def injection_calc(self):
        inj = Injection()
        inj.cur_iob_units = self.get_cur_iob_units()
        inj.cur_bg_units = self.get_cur_bg_units()  # units over or under target bg
        inj.cur_bg = self.get_current_bg()
        inj.carbs_units, inj.courses_to_cover = self.get_carbs_to_cover_units()
        inj.carbs_to_cover = 0
        for course in inj.courses_to_cover:
            inj.carbs_to_cover += course[0]
        inj.low_limit_units = self.cloop_config.get_low_limit_units()
        inj.temp_duration = self.cloop_config.get_temp_duration()
        inj.cur_basal_units = self.cloop_config.get_cur_basal_units()
        inj.all_meal_carbs_absorbed = self.get_all_meal_carbs_absorbed()

        if inj.cur_bg_units is None:
            inj.correction_units = 0
        else:
            inj.correction_units = inj.cur_bg_units - inj.cur_iob_units
            # if still have carbs to absorb and not low and correction would be negative
            # then do not correct as the carbs to absorb should cover iob
            if not inj.all_meal_carbs_absorbed and inj.low_limit_units < inj.cur_bg_units < inj.cur_iob_units:
                inj.correction_units = 0

        inj.injection_units = inj.carbs_units + inj.correction_units

        if .5 > inj.injection_units > -.5:
            logging.info("Injections units too little to take action : " + str(inj.injection_units))
            logging.info("Carbs_absorbed (" + str(inj.all_meal_carbs_absorbed) + ") cur_bg ("
                         + str(inj.cur_bg) + "=" + str(inj.cur_bg_units) + ") carbs_to_cover ("
                         + str(inj.carbs_to_cover) + "=" + str(inj.carbs_units) + ") cur_iob_units ("
                         + str(inj.cur_iob_units) + ") correction_units (" + str(inj.correction_units) + ")")
            return None

        if inj.injection_units > 0:
            # do a bolus
            if self.is_recent_injection("bolus"):
                return None
            inj.injection_type = "bolus"
            inj.injection_units = round(inj.injection_units, 2)
            inj.temp_rate = None
        else:
            # do a temp rate
            if self.is_recent_injection("square"):
                return None
            inj.injection_type = "square"
            inj.temp_rate_units = inj.cur_basal_units + inj.injection_units
            if inj.temp_rate_units < 0:
                inj.temp_rate = 0
            else:
                inj.temp_rate = (inj.cur_basal_units + inj.injection_units) * (60 / inj.temp_duration)
            inj.temp_rate = round(inj.temp_rate, 2)

        return inj
        # don't do injections if off or lows only

    def do_injection(self, inj, automode):
        # do the injection if needed
        if inj.injection_type is None or inj.injection_type == "none_needed":
            logging.info(
                "Returning from injection_process: No injection required. Automode is "
                + automode + " and injection type is " + inj.injection_type)
            return None
        elif inj.injection_type == "bolus" and automode == "fullOn":
            # use a bolus to inject
            inj.injection_id = self.create_injection_rec(inj)
            inj.successfully_executed = self.do_bolus(inj.injection_units)
            inj.attempted = True
        elif inj.injection_type == "square" and (automode == "fullOn" or automode == "lowsOnly"):
            # use a temp rate to remove insulin
            inj.injection_id = self.create_injection_rec(inj)
            inj.successfully_executed = self.set_temp_basal(inj.temp_rate, self.cloop_config.get_temp_duration())
            inj.attempted = True
        else:
            inj.attempted = False
        return inj

    def add_inj_alert_and_log(self, inj):
        if inj.attempted:
            # if injection actually happened mark as such
            self.injection_attempted_alert_log(inj)
        else:
            # otherwise just alert to take action
            self.injection_recommended_alert_log(inj)

    def injection_attempted_alert_log(self, inj):
        # inj.injection_id = self.create_injection_rec(inj)
        # if injection actually happened then set it, add alert info
        if not inj.successfully_executed:
            sql = "update injections set status = 'failed - not delivered', transferred = 'no' \
                where injection_id = " + str(inj.injection_id)
            self.cloop_db.execute(sql)
            self.cloop_db.log("FAIL", "injection_process",
                              "Unable to execute " + inj.injection_type + " injection #" + str(
                                  inj.injection_id) + " of " + str(inj.injection_units) + "u temp:"
                              + str(inj.temp_rate))
            return
        else:
            sql = "update injections set status = 'delivered', transferred = 'no' \
                where injection_id = " + str(inj.injection_id)
            self.cloop_db.execute(sql)
            self.mark_courses_for_injection(inj)
            if inj.injection_type == "square":
                self.add_alert(now, "process_injection", "info", "Set Temp " + str(inj.temp_rate),
                               "Injection #" + str(inj.injection_id) + " with a rate of " +
                               str(inj.temp_rate) + " was given at " + str(now))
            else:
                self.add_alert(now, "process_injection", "info", "Injected " + str(inj.injection_units) + "u",
                               "Injection #" + str(inj.injection_id) + " of " +
                               str(inj.injection_units) + " units was given at " + str(now))
            self.add_time_to_eat_alert(inj)
            self.cloop_db.log("SUCCESS", "injection_process",
                              "Successfully able to execute " + inj.injection_type
                              + " injection #" + str(inj.injection_id) + " of " + str(
                                  inj.injection_units) + "u temp:" + str(inj.temp_rate))

    def injection_recommended_alert_log(self, inj):
        self.cloop_db.log("INFO", "injection_process",
                          inj.injection_type + " injection of " + str(inj.injection_units)
                          + "u recommended at " + str(now) + " cur_bg:" + str(inj.cur_bg) + "-" + str(inj.cur_bg_units)
                          + " iob:" + str(inj.cur_iob_units) + " meal_over:" + str(inj.all_meal_carbs_absorbed))
        if not self.is_recent_recommendation(inj.injection_type):
            if inj.injection_type == "square":
                self.add_alert(now, "process_injection.should_temp", "warning", "Set Temp " + str(inj.temp_rate),
                               "Should set a temporary rate of " + str(inj.temp_rate))
            else:
                self.add_alert(now, "process_injection.should_bolus", "warning",
                               "Inject : " + str(inj.injection_units) + "u",
                               "Should inject " + str(inj.injection_units) + "u at "
                               + str(now) + " for " + str(inj.carbs_to_cover) + "g - " + str(inj.cur_bg))
                self.add_time_to_eat_alert(inj)

    def add_alert(self, datetime_to_alert, code, alert_type, title, message):
        sql_to_insert = "insert into alerts (datetime_recorded, datetime_to_alert, " \
                        "src, code, type, title, message, transferred) " \
                        "values (now(), '" + str(datetime_to_alert) + "','device','" \
                        + code + "','" + alert_type + "','" + title + "','" + message + "','no')"
        self.cloop_db.execute(sql_to_insert)

    def create_injection_rec(self, inj):
        if inj.injection_type == "bolus":
            # units_delivered = inj.units_intended
            inj.temp_rate = "null"
            # else:
            #units_delivered = inj.temp_rate / (60 / self.cloop_config.get_temp_duration()) - self.get_cur_basal_units(
            #    self.cloop_config.get_temp_duration())
        if inj.cur_bg_units is None:
            inj.cur_bg_units = "null"
        if inj.cur_bg is None:
            inj.cur_bg = "null"
        sql_to_insert = "insert into injections (injection_type, \
                    units_intended, temp_rate, datetime_intended, \
                    cur_iob_units, cur_bg_units, \
                    cur_bg, correction_units, \
                    carbs_to_cover, carbs_units, \
                    cur_basal_units, all_meal_carbs_absorbed, \
                    status, transferred) values ( '" + inj.injection_type + "'," \
                        + str(inj.injection_units) + "," + str(inj.temp_rate) + ",now()," \
                        + str(inj.cur_iob_units) + "," + str(inj.cur_bg_units) \
                        + "," + str(inj.cur_bg) + "," + str(inj.correction_units) \
                        + "," + str(inj.carbs_to_cover) + "," + str(inj.carbs_units) + "," \
                        + str(inj.cur_basal_units) + ",'" + str(inj.all_meal_carbs_absorbed) \
                        + "','delivered','no')"
        self.cloop_db.execute(sql_to_insert)
        injection_ids = self.cloop_db.select("select max(injection_id) from injections")
        injection_id = injection_ids[0][0]
        return injection_id

    # get the current Insulin On Board
    def get_cur_iob_units(self):
        sql_select_iob = "select datetime_iob, iob from iob where \
                        datetime_iob = (select max(datetime_iob) from iob \
                        where datetime_iob < now() and datetime_iob > now() - interval 10 minute)"
        cur_iobs = self.cloop_db.select(sql_select_iob)
        iob = 0
        for row in cur_iobs:
            iob = row[1]
        return iob

    def get_cur_bg_units(self):
        sql_select_sgvs = "select datetime_recorded, sgv from sgvs where \
                        datetime_recorded = (select max(datetime_recorded) from sgvs \
                        where datetime_recorded < now() and datetime_recorded > now() - interval 20 minute)"
        sgvs = self.cloop_db.select(sql_select_sgvs)
        cur_bg = None
        for row in sgvs:
            cur_bg = row[1]
        if cur_bg is None:
            return None
        cur_bg_units = (cur_bg - self.cloop_config.get_target_bg()) / self.cloop_config.get_bg_sensitivity()
        return cur_bg_units

    def get_carbs_to_cover_units(self):
        courses_to_cover = self.get_courses_to_cover()
        carbs = 0
        for course in courses_to_cover:
            carbs += course[0]
        carbs_units = carbs / self.cloop_config.get_carb_sensitivity()
        return carbs_units, courses_to_cover

    def get_courses_to_cover(self):
        """
            Get the courses which should be covered with an injection
            :return: list of courses [carbs, course_id]
            """
        sql_get_courses = "select carbs, course_id from courses where \
                        datetime_consumption > now() - interval 25 minute \
                        and datetime_consumption < now() + interval 40 minute \
                        and course_id not in (select course_id from courses_to_injections where \
                        injection_id in (select injection_id from injections where status in ('successful', 'delivered')))"
        return self.cloop_db.select(sql_get_courses)

    def mark_courses_for_injection(self, inj):
        if len(inj.courses_to_cover) <= 0:
            return
        sql_to_mark = "insert into courses_to_injections (course_id, injection_id) values "
        for course in inj.courses_to_cover:
            sql_to_mark += "(" + str(course[1]) + ", " + str(inj.injection_id) + "),"
        sql_to_mark = sql_to_mark[:-1]
        self.cloop_db.execute(sql_to_mark)

    def get_all_meal_carbs_absorbed(self):
        sql_get_last_injection = "select datetime_delivered from injections " \
                                 "where status in ('successful','delivered') and " \
                                 "injection_id in (select distinct injection_id from courses_to_injections) " \
                                 "order by datetime_delivered desc limit 1"
        injs = self.cloop_db.select(sql_get_last_injection)
        last_injection = None
        for row in injs:
            last_injection = row
        if last_injection is None:
            return True
        if last_injection[0] < now + datetime.timedelta(hours=-2):
            return True
        else:
            return False

    def get_current_bg(self):
        sql_select_sgvs = "select datetime_recorded, sgv from sgvs where \
                        datetime_recorded = (select max(datetime_recorded) from sgvs \
                        where datetime_recorded < now() and datetime_recorded > now()- interval 20 minute)"
        sgvs = self.cloop_db.select(sql_select_sgvs)
        cur_bg = None
        for row in sgvs:
            cur_bg = row[1]
        return cur_bg

    def is_recent_injection(self, bolus_type):
        if bolus_type == "bolus":
            # if simulating bolus don't want quite as many alerts
            if self.get_automode() == "simulate":
                time_interval = "45"
            else:
                time_interval = "15"
            sql_get_active_injections = "select * from injections where \
                        status in ('successful','delivered') and datetime_delivered > now() - interval " + time_interval + " minute \
                            and injection_type = 'square'"

        else:
            sql_get_active_injections = "select * from injections where \
                        status = 'successful' and datetime_delivered > now() - interval 31 minute \
                                        and injection_type = 'square'"
        rows = self.cloop_db.select(sql_get_active_injections)
        if len(rows) > 0:
            return True
        else:
            return False

    def get_courses_covered(self, injection_id):
        sql_get_courses = "select carbs, course_id from courses where course_id in \
                (select course_id from courses_to_injections where injection_id = " + str(injection_id) + ")"
        return self.cloop_db.select(sql_get_courses)

    def get_automode(self):
        sql_get_automode = "select is_on from automode_switch order by datetime_recorded desc limit 1"
        rows = self.cloop_db.select(sql_get_automode)
        if rows is None or len(rows) <= 0:
            return "off"
        row = rows[0]
        is_on = row[0]
        return is_on

    def do_bolus(self, injection_units):
        if windowsConfig:
            return True
        pump = pump_interface.PumpInterface()
        result = pump.do_bolus(injection_units)
        if result == "Successful":
            return True
        else:
            return False

    def set_temp_basal(self, temp_rate, duration):
        if windowsConfig:
            return True
        pump = pump_interface.PumpInterface()
        result = pump.set_temp_basal(temp_rate, duration)
        if result == "Successful":
            return True
        else:
            return False

    def is_recent_recommendation(self, injection_type):
        sql = "select count(*) from alerts where datetime_recorded > now() - interval 45 minute and code = "
        if injection_type == "bolus":
            sql += "'injection_process.should_bolus'"
        else:
            sql += "'injection_process.should_temp'"
        rows = self.cloop_db.select(sql)
        if rows is None or len(rows) <= 0 or rows[0][0] == 0:
            return False
        else:
            return True

    def add_time_to_eat_alert(self, inj):
        if inj.carbs_to_cover is not None and inj.carbs_to_cover != 0:
            self.add_alert(now + datetime.timedelta(minutes=35), "process_injection", "alert", "Time to eat",
                           "Try to eat " + str(inj.carbs_to_cover) + "g of carbs for injection "
                           + str(inj.injection_id) + " in 5 minutes")


if __name__ == '__main__':
    logging.info("Injection process started")
    process = InjectionProcess()
    process.process_injection()
    logging.info("Injection process ended")

