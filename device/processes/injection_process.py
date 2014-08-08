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
    # device config
    logging.basicConfig(filename=currentDate + '.injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # windows config
    logging.basicConfig(filename='./log/' + currentDate + '.injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class InjectionProcess():
    db_host = "localhost"
    if windowsConfig:
        db_port = 33062  # windows config
    else:
        db_port = 3306  # device config
    db_user = "root"
    db_pass = "raspberry"
    db_db = "cloop"
    cloop_config = None

    def __init__(self):
        self.db_conn = MySQLdb.connect(host=self.db_host,
                                       port=self.db_port,
                                       user=self.db_user,
                                       passwd=self.db_pass,
                                       db=self.db_db)
        self.db = self.db_conn.cursor()
        self.cloop_config = cloop_config.CloopConfig()

    def __del__(self):
        self.db.close()
        self.db_conn.close()

    def process_injection(self):
        if not self.is_in_automode():
            sys.exit()
        if self.is_active_injection():
            sys.exit()
        # if there is a course coming wait a little to correct and cover the carbs
        if self.should_wait_for_course():
            sys.exit()
        temp_rate, injection_id = self.get_injection_amount()
        if temp_rate is None:
            logging.info("Returning from injection_process: No injection required")
            return
        successfully_executed = self.set_temp_basal(temp_rate, self.cloop_config.get_temp_duration())
        if not successfully_executed:
            # log in bd and exit
            sql_fail_injection = "update injections set status = 'failed', transferred = 'no' where injection_id = " \
                                 + str(injection_id)
            self.db.execute(sql_fail_injection)
            logging.info("SQL: " + sql_fail_injection)
            self.db_conn.commit()
        else:
            # successful:
            sql_success_injection = "update injections set status = 'successful', transferred = 'no', \
                datetime_delivered = now() where injection_id = " + str(injection_id)
            self.db.execute(sql_success_injection)
            logging.info("SQL: " + sql_success_injection)
            self.db_conn.commit()
            self.set_iob(injection_id)
            self.add_alert(now, "process_injection", "info", "Injection " + str(injection_id) + " of a rate of " +
                           str(temp_rate) + " was given at " + str(now))
            courses = self.get_courses_covered(injection_id)
            if len(courses) > 0:
                carbs = 0
                for course in courses:
                    carbs += course[0]
                self.add_alert(now + datetime.timedelta(minutes=45), "process_injection", "alert", "Time to eat " +
                               str(carbs) + "g of carbs for injection " + str(injection_id))

    # return the amount insulin that should be injected
    # return null if no injection needed
    # based on carbs, IOB, current BG (if CGM accurate), carb sensitivity, bg sensitivity
    def get_injection_amount(self):
        cur_iob_units = self.get_cur_iob_units()
        cur_bg_units = self.get_cur_bg_units()  # units over or under target bg
        carbs_units, courses_to_cover = self.get_carbs_to_cover_units()
        carbs_to_cover = 0
        for course in courses_to_cover:
            carbs_to_cover += course[0]
        low_limit_units = self.cloop_config.get_low_limit_units()
        temp_duration = self.cloop_config.get_temp_duration()
        cur_basal_units = self.get_cur_basal_units(self.cloop_config.get_temp_duration())
        all_meal_carbs_absorbed = self.get_all_meal_carbs_absorbed()

        if cur_bg_units is None:
            correction_units = 0
        else:
            correction_units = cur_bg_units - cur_iob_units
            if all_meal_carbs_absorbed and low_limit_units < cur_bg_units < cur_iob_units:
                correction_units = 0

        injection_units = carbs_units + correction_units
        temp_rate_units = cur_basal_units + injection_units
        if temp_rate_units < 0:
            temp_rate = 0
        else:
            temp_rate = (cur_basal_units + injection_units) * (60 / temp_duration)
        temp_rate = round(temp_rate, 2)

        temp_rate_thresh = .05
        lower_thresh = self.get_cur_basal_units() - temp_rate_thresh
        upper_thresh = self.get_cur_basal_units() + temp_rate_thresh
        if self.get_cur_basal_units() - temp_rate_thresh < temp_rate / (60 / temp_duration) < self.get_cur_basal_units() + temp_rate_thresh:
            logging.info("Temp Rate desired close to current basal => no injection temp_rate: " + str(
                temp_rate) + " basal: " + str(self.get_cur_basal_units()))
            return None, None
        # log all data into db

        # insert meals to injection records
        injection_id = self.create_injection_rec(cur_iob_units, cur_bg_units,
                                                 carbs_to_cover, carbs_units,
                                                 cur_basal_units, all_meal_carbs_absorbed,
                                                 correction_units, injection_units,
                                                 temp_rate)
        self.mark_courses_for_injection(courses_to_cover, injection_id)
        return temp_rate, injection_id

    def set_temp_basal(self, temp_rate, duration):
        pump = pump_interface.PumpInterface()
        result = pump.set_temp_basal(temp_rate, duration)
        if result != "Successful":
            return True
        else:
            return False

    def set_iob(self, injection_id):
        # create iob based on iob dist in db (max 6 hrs)
        # iterate by 5 min intervals
        self.db.execute("select max(iob_dist.interval) from iob_dist")
        max_interval = self.db.fetchall()[0][0]
        for i in range(0, max_interval + 5, 5):
            sql_save_iob = "insert into iob (datetime_iob, iob) values \
            (\
              from_unixtime(round(UNIX_TIMESTAMP( \
                (select datetime_delivered from injections where injection_id = " + str(
                injection_id) + ")+ interval " + str(i) + " minute \
                 )/300)*300),\
              ifnull((select units_delivered * (select iob_dist_pct from iob_dist \
                    where iob_dist.interval = " + str(i) + ") / 100 \
                from injections where injection_id = " + str(injection_id) + "),0) \
            ) \
            on duplicate key update transferred = 'no', \
            iob = iob + \
              ifnull((select units_delivered * (select iob_dist_pct from iob_dist where iob_dist.interval = " \
                           + str(i) + ") / 100 \
              from injections where injection_id = " + str(injection_id) + "),0)"
            logging.info("SQL: " + sql_save_iob)
            self.db.execute(sql_save_iob)
            self.db_conn.commit()

    def add_alert(self, datetime_to_alert, code, alert_type, message):
        sql_to_insert = "insert into alerts (datetime_to_alert, src, code, type, message, transferred) values \
                ('" + str(datetime_to_alert) + "','device','" + code + "','" + alert_type + "','" + message + "','no')"
        logging.info("SQL: " + sql_to_insert)
        self.db.execute(sql_to_insert)
        self.db_conn.commit()

    def create_injection_rec(self, cur_iob_units, cur_bg_units,
                             carbs_to_cover, carbs_units,
                             cur_basal_units, all_meal_carbs_absorbed,
                             correction_units, units_intended,
                             temp_rate):
        units_delivered = temp_rate / (60 / self.cloop_config.get_temp_duration()) - self.get_cur_basal_units(
            self.cloop_config.get_temp_duration())
        if cur_bg_units is None:
            cur_bg_units = "null"
        sql_to_insert = "insert into injections (\
                    units_intended, units_delivered, temp_rate, datetime_intended, \
                    cur_iob_units, cur_bg_units, correction_units, \
                    carbs_to_cover, carbs_units, \
                    cur_basal_units, all_meal_carbs_absorbed, \
                    status, transferred) values ( " \
                        + str(units_intended) + "," + str(units_delivered) + "," + str(temp_rate) + ",now()," \
                        + str(cur_iob_units) + "," + str(cur_bg_units) + "," + str(correction_units) \
                        + "," + str(carbs_to_cover) + "," + str(carbs_units) + "," \
                        + str(cur_basal_units) + ",'" + str(all_meal_carbs_absorbed) \
                        + "','initial','awaiting completion')"
        logging.info("SQL: " + sql_to_insert)
        self.db.execute(sql_to_insert)
        self.db_conn.commit()
        self.db.execute("select max(injection_id) from injections")
        injection_ids = self.db.fetchall()
        injection_id = injection_ids[0][0]
        return injection_id

    # get the current Insulin On Board
    def get_cur_iob_units(self):
        sql_select_iob = "select datetime_iob, iob from iob where \
                        datetime_iob = (select max(datetime_iob) from iob \
                        where datetime_iob < now() and datetime_iob > now() - interval 10 minute)"
        logging.info("SQL: " + sql_select_iob)
        self.db.execute(sql_select_iob)
        iob = 0
        for row in self.db.fetchall():
            iob = row[1]
        return iob

    def get_cur_bg_units(self):
        sql_select_sgvs = "select datetime_recorded, sgv from sgvs where \
                        datetime_recorded = (select max(datetime_recorded) from sgvs \
                        where datetime_recorded < now() and datetime_recorded > now() - interval 20 minute)"
        logging.info("SQL: " + sql_select_sgvs)
        self.db.execute(sql_select_sgvs)
        cur_bg = None
        for row in self.db.fetchall():
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
                        datetime_consumption > now() - interval 45 minute \
                        and datetime_consumption < now() + interval 50 minute \
                        and course_id not in (select course_id from courses_to_injections where \
                        injection_id in (select injection_id from injections where status = 'successful'))"
        logging.info("SQL: " + sql_get_courses)
        self.db.execute(sql_get_courses)
        return self.db.fetchall()

    def should_wait_for_course(self):
        sql_to_wait = "select * from courses where \
                        datetime_consumption > now()+ interval 50 minute \
                        and datetime_consumption < now()+ interval 75 minute \
                        and course_id not in (select course_id from courses_to_injections where \
                        injection_id in (select injection_id from injections where status = 'successful'))"
        logging.info("SQL: " + sql_to_wait)
        self.db.execute(sql_to_wait)
        rows = self.db.fetchall()
        if len(rows) > 0:
            return True
        else:
            return False

    def mark_courses_for_injection(self, courses, injection_id):
        if len(courses) <= 0:
            return
        sql_to_mark = "insert into courses_to_injections (course_id, injection_id) values "
        for course in courses:
            sql_to_mark += "(" + str(course[1]) + ", " + str(injection_id) + "),"
        sql_to_mark = sql_to_mark[:-1]
        logging.info("SQL: " + sql_to_mark)
        self.db.execute(sql_to_mark)
        self.db_conn.commit()

    def get_cur_basal_units(self, duration=None):
        if duration is None:
            duration = self.cloop_config.get_temp_duration()
        return 1.1 * (duration / 60)

    def get_all_meal_carbs_absorbed(self):
        sql_get_last_injection = "select datetime_delivered from injections where status = 'successful' and \
                        injection_id in (select distinct injection_id from courses_to_injections) \
                        order by datetime_delivered desc limit 1"
        logging.info("SQL: " + sql_get_last_injection)
        self.db.execute(sql_get_last_injection)
        last_injection = None
        for row in self.db.fetchall():
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
                        where datetime_recorded < now() and datetime_iob > now()- interval 20 minute)"
        logging.info("SQL: " + sql_select_sgvs)
        self.db.execute(sql_select_sgvs)
        cur_bg = None
        for row in self.db.fetchall():
            cur_bg = row[1]
        return cur_bg

    def is_active_injection(self):
        sql_get_active_injections = "select * from injections where \
                        status = 'successful' and datetime_delivered > now() - interval 31 minute"
        logging.info("SQL: " + sql_get_active_injections)
        self.db.execute(sql_get_active_injections)
        rows = self.db.fetchall()
        if len(rows) > 0:
            return True
        else:
            return False

    def get_courses_covered(self, injection_id):
        sql_get_courses = "select carbs, course_id from courses where course_id in \
                (select course_id from courses_to_injections where injection_id = " + str(injection_id) + ")"
        logging.info("SQL: " + sql_get_courses)
        self.db.execute(sql_get_courses)
        return self.db.fetchall()

    def is_in_automode(self):
        sql_get_automode = "select is_on from automode_switch order by datetime_recorded desc limit 1"
        logging.info("SQL: " + sql_get_automode)
        self.db.execute(sql_get_automode)
        rows = self.db.fetchall()
        row = rows[0]
        is_on = row[0]
        if is_on == "yes":
            return True
        else:
            return False  # if __name__ == '__main__':


if __name__ == '__main__':
    logging.info("Injection process started")
    process = InjectionProcess()
    process.process_injection()
    logging.info("Injection process ended")

"""
process = InjectionProcess()
failed = False
# test 1 - meal without iob
process.db.execute("delete from courses_to_injections")
process.db.execute("delete from injections")
process.db.execute("delete from iob")
process.db.execute("delete from courses")
process.db.execute("delete from sgvs")
process.db.execute("delete from automode_switch")
process.db.execute("delete from alerts")
process.db.execute(
    "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
process.db.execute(
    "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
process.db.execute(
    "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes');")
process.db_conn.commit()
process.process_injection()
process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
rows = process.db.fetchall()
if len(rows) > 1:
    failed = True
    print "Fail: Test 1: too many injections ("+str(len(rows))+")"
if rows[0][0] != 4.30769:
    failed = True
    print "Fail: Test 1: units_intended wrong ("+str(rows[0][0])+")"
if rows[0][1] != 4.30769:
    failed = True
    print "Fail: Test 1: units_delivered wrong ("+str(rows[0][1])+")"
if rows[0][2] != 9.71538:
    failed = True
    print "Fail: Test 1: temp_rate wrong ("+str(rows[0][2])+")"
if rows[0][3] != "successful":
    failed = True
    print "Fail: Test 1: status wrong ("+str(rows[0][3])+")"

process.db.execute("select iob, datetime_iob from iob")
rows = process.db.fetchall()
if len(rows) != 41:
    failed = True
    print "Fail: Test 1: wrong number of rows for iob ("+str(len(rows))+")"

process.db.execute("select * from alerts")
rows = process.db.fetchall()
if len(rows) != 2:
    failed = True
    print "Fail: Test 1: wrong number of rows for alerts ("+str(len(rows))+")"
if failed:
    print "Fail: Test 1 (meal without iob) failed thus stopping"
    sys.exit()
else:
    print "Test 1 (meal without iob) passes!"

# Test 2 - meal with iob
process.db.execute("delete from courses_to_injections")
process.db.execute("delete from injections")
process.db.execute("delete from iob")
process.db.execute("delete from courses")
process.db.execute("delete from sgvs")
process.db.execute("delete from automode_switch")
process.db.execute("delete from alerts")
process.db.execute(
    "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
process.db.execute(
    "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
process.db.execute(
    "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes')")
process.db.execute(
    "insert into iob (datetime_iob, iob) values \
    (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2), \
    (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2), \
    (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10)")
process.db_conn.commit()
process.process_injection()
process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
rows = process.db.fetchall()
if len(rows) > 1:
    failed = True
    print "Fail: Test 2: too many injections ("+str(len(rows))+")"
if rows[0][0] != 2.30769:
    failed = True
    print "Fail: Test 2: units_intended wrong ("+str(rows[0][0])+")"
if rows[0][1] != 2.30769:
    failed = True
    print "Fail: Test 2: units_delivered wrong ("+str(rows[0][1])+")"
if rows[0][2] != 5.71538:
    failed = True
    print "Fail: Test 2: temp_rate wrong ("+str(rows[0][2])+")"
if rows[0][3] != "successful":
    failed = True
    print "Fail: Test 2: status wrong ("+str(rows[0][3])+")"

process.db.execute("select iob, datetime_iob from iob")
rows = process.db.fetchall()
if len(rows) != 42:
    failed = True
    print "Fail: Test 2: wrong number of rows for iob ("+str(len(rows))+")"

process.db.execute("select * from alerts")
rows = process.db.fetchall()
if len(rows) != 2:
    failed = True
    print "Fail: Test 2: wrong number of rows for alerts ("+str(len(rows))+")"

if failed:
    print "Fail: Test 2 (meal with iob) failed thus stopping"
    sys.exit()
else:
    print "Test 2 (meal with iob) passes!"



# Test 3 - negative correction
process.db.execute("delete from courses_to_injections")
process.db.execute("delete from injections")
process.db.execute("delete from iob")
process.db.execute("delete from courses")
process.db.execute("delete from sgvs")
process.db.execute("delete from automode_switch")
process.db.execute("delete from alerts")
process.db.execute(
    "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
process.db.execute(
    "insert into courses (course_id, carbs, datetime_consumption) values (1, 10, now() + interval 5 minute)")
process.db.execute(
    "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes')")
process.db.execute(
    "insert into iob (datetime_iob, iob) values \
    (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2), \
    (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2), \
    (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10)")
process.db_conn.commit()
process.process_injection()
process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
rows = process.db.fetchall()
if len(rows) > 1:
    failed = True
    print "Fail: Test 3: too many injections ("+str(len(rows))+")"
if rows[0][0] != -3.23077:
    failed = True
    print "Fail: Test 3: units_intended wrong ("+str(rows[0][0])+")"
if rows[0][1] != -0.55:
    failed = True
    print "Fail: Test 2: units_delivered wrong ("+str(rows[0][1])+")"
if rows[0][2] != 0.0:
    failed = True
    print "Fail: Test 3: temp_rate wrong ("+str(rows[0][2])+")"
if rows[0][3] != "successful":
    failed = True
    print "Fail: Test 3: status wrong ("+str(rows[0][3])+")"

process.db.execute("select iob, datetime_iob from iob")
rows = process.db.fetchall()
if len(rows) != 42:
    failed = True
    print "Fail: Test 3: wrong number of rows for iob ("+str(len(rows))+")"

process.db.execute("select * from alerts")
rows = process.db.fetchall()
if len(rows) != 2:
    failed = True
    print "Fail: Test 3: wrong number of rows for alerts ("+str(len(rows))+")"

if failed:
    print "Fail: Test 3 (negative correction) failed thus stopping"
    sys.exit()
else:
    print "Test 3 (negative correction) passes!"



# Test 4 - no sgv / high carbs
process.db.execute("delete from courses_to_injections")
process.db.execute("delete from injections")
process.db.execute("delete from iob")
process.db.execute("delete from courses")
process.db.execute("delete from sgvs")
process.db.execute("delete from automode_switch")
process.db.execute("delete from alerts")
process.db.execute(
    "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
process.db.execute(
    "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes')")
process.db.execute(
    "insert into iob (datetime_iob, iob) values \
    (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2), \
    (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2), \
    (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10)")
process.db_conn.commit()
process.process_injection()
process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
rows = process.db.fetchall()
if len(rows) > 1:
    failed = True
    print "Fail: Test 4: too many injections ("+str(len(rows))+")"
if rows[0][0] != 6.92308:
    failed = True
    print "Fail: Test 4: units_intended wrong ("+str(rows[0][0])+")"
if rows[0][1] != 6.92308:
    failed = True
    print "Fail: Test 2: units_delivered wrong ("+str(rows[0][1])+")"
if rows[0][2] != 14.9462:
    failed = True
    print "Fail: Test 4: temp_rate wrong ("+str(rows[0][2])+")"
if rows[0][3] != "successful":
    failed = True
    print "Fail: Test 4: status wrong ("+str(rows[0][3])+")"

process.db.execute("select iob, datetime_iob from iob")
rows = process.db.fetchall()
if len(rows) != 42:
    failed = True
    print "Fail: Test 4: wrong number of rows for iob ("+str(len(rows))+")"

process.db.execute("select * from alerts")
rows = process.db.fetchall()
if len(rows) != 2:
    failed = True
    print "Fail: Test 4: wrong number of rows for alerts ("+str(len(rows))+")"

if failed:
    print "Fail: Test 4 (no sgv / high carbs) failed thus stopping"
    sys.exit()
else:
    print "Test 4 (no sgv / high carbs) passes!"
"""