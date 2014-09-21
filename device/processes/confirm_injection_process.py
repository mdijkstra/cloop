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
import cloop_db
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
    # windows config
    logging.basicConfig(filename=currentDate + '-injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # device config
    logging.basicConfig(filename="./log/" + currentDate + '-injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class ConfirmInjectionProcess():
    cloop_config = cloop_config.CloopConfig()
    cloop_db = cloop_db.CloopDB()
    pump_interface = pump_interface.PumpInterface()

    def run(self, include_init=True):
        # download from pump
        recent_data = self.pump_interface.get_mm_latest(include_init=include_init, recent_minutes=240)
        # for each injection get time
        if recent_data is not None:
            logging.info("Found "+str(len(recent_data))+" pieces of recent data. Processing..."+str(recent_data))
            for i in range(0, len(recent_data), 1):
                record1 = recent_data[i]
                if record1["_type"] == "Bolus":
                    self.save_or_update_injection(record1, None)
                if record1["_type"] == "TempBasal" and i + 1 < len(recent_data) \
                        and recent_data[i + 1]["_type"] == "TempBasalDuration":
                    record2 = recent_data[i + 1]
                    self.save_or_update_injection(record1, record2)
        else:
            logging.info("Didn't find any recent data")
        self.update_iob()
        self.set_old_injs_to_fail()

    def save_or_update_injection(self, record1, record2):
        units_intended, units_delivered = self.get_units_from_records(record1, record2)
        sql = "select injection_id, status from injections " \
              "where datetime_intended + interval 3 minute > '" + record1["timestamp"] \
              + "' and datetime_intended - interval 3 minute < '" + record1["timestamp"] \
              + "' and abs(units_intended - " + units_intended + ") <= 1e-6 " \
              + " order by datetime_intended asc limit 1"
        possible_injs = self.cloop_db.select(sql)
        # if no existing injection
        if possible_injs is None or len(possible_injs) <= 0:
            self.new_inj_from_json(record1, record2)
        elif possible_injs[0][1] != "confirmed" and possible_injs[0][1] != "successful":
            self.confirm_inj(record1, record2, possible_injs[0][0])
        else:
            logging.info("Injection #"+str(possible_injs[0][0])+" already successful.")

    def update_iob(self):
        max_interval_square, max_interval_bolus = self.get_max_intervals()
        sql = "select injection_id, units_delivered, datetime_delivered, injection_type " \
              "from injections where status = 'confirmed'"
        injs = self.cloop_db.select(sql)
        if injs is None or len(injs) <= 0:
            return
        for inj in injs:
            injection_id = inj[0]
            units_delivered = inj[1]
            datetime_delivered = inj[2]
            injection_type = inj[3]
            # create iob based on iob dist in db
            # iterate by 5 min intervals
            logging.info("Setting IOB for injection #" + str(injection_id))
            if injection_type == 'bolus':
                max_interval = max_interval_bolus
            else:
                max_interval = max_interval_square
            for interval in range(0, max_interval + 5, 5):
                sql_set_iob = self.get_sql_set_iob(interval, datetime_delivered, units_delivered, injection_type)
                self.cloop_db.execute(sql_set_iob)
            # set injection successfully completed
            self.cloop_db.execute(
                "update injections set status = 'successful', transferred='no' where injection_id=" + str(injection_id))
        self.set_iob_bg()

    def get_sql_set_iob(self, interval, datetime_delivered, units_delivered, injection_type):
        sql_set_iob = "insert into iob (datetime_iob, iob, iob_bg) values ( " \
                      + "from_unixtime(round(UNIX_TIMESTAMP( '" \
                      + str(datetime_delivered) + "' + interval " + str(interval) + " minute )/300)*300), " \
                      + "ifnull((" + str(units_delivered) + " * " \
                      + "(select iob_dist_pct from iob_dist where iob_dist.interval = " + str(interval) \
                      + " and injection_type='" + injection_type + "') / 100),0) \
                                , 0) \
                on duplicate key update transferred = 'no', \
                    iob = iob + \
                    ifnull((" + str(units_delivered) + " * " + \
                      "(select iob_dist_pct from iob_dist where iob_dist.interval = " + str(interval) + \
                      " and injection_type='" + injection_type + "') / 100),0), iob_bg = 0"
        return sql_set_iob

    def set_old_injs_to_fail(self):
        sql = "update injections set status = 'failed - not confirmed', transferred = 'no' " \
              "where datetime_intended < now() - interval 20 minute and status = 'delivered'"
        self.cloop_db.execute(sql)

    def new_inj_from_json(self, record1, record2):
        if record1["_type"] == "Bolus":
            injection_type = "bolus"
        else:
            injection_type = "square"
        units_intended, units_delivered = self.get_units_from_records(record1, record2)
        datetime_intended = record1["timestamp"]
        datetime_delivered = record1["timestamp"]

        sql = "insert into injections (injection_type," \
              "units_intended, units_delivered, " \
              "datetime_intended, datetime_delivered," \
              "status) values " \
              "( '" + injection_type + "'," \
              + units_intended + "," + units_delivered + ",'" + \
              datetime_intended + "','" + datetime_delivered + \
              "','confirmed')"
        self.cloop_db.execute(sql)

    def confirm_inj(self, record1, record2, injection_id):
        units_intended, units_delivered = self.get_units_from_records(record1, record2)
        sql = "update injections set datetime_delivered = '" + str(record1["timestamp"]) \
              + "', units_delivered = " + units_delivered \
              + ", status = 'confirmed', transferred = 'no'" \
              + " where injection_id = " + str(injection_id)
        self.cloop_db.execute(sql)

    def get_units_from_records(self, record1, record2):
        if record1["_type"] == "Bolus":
            units_intended = str(record1["programmed"])
            units_delivered = str(record1["amount"])
        else:
            units_intended = (record1["rate"] - self.cloop_config.get_cur_basal_units(60)) * \
                             (record2["duration (min)"] / 60)
            units_delivered = units_intended
        return str(units_intended), str(units_delivered)

    def get_max_intervals(self):
        temp = self.cloop_db.select("select max(iob_dist.interval) from iob_dist where injection_type = 'bolus'")
        max_interval_bolus = temp[0][0]
        temp = self.cloop_db.select("select max(iob_dist.interval) from iob_dist where injection_type = 'square'")
        max_interval_square = temp[0][0]
        return max_interval_square, max_interval_bolus

    def set_iob_bg(self):
        bg_sensitivity = self.cloop_config.get_bg_sensitivity()
        bg_target = self.cloop_config.get_target_bg()
        sql_set_iob_bg = "update iob set transferred = 'no'," \
                         + " iob_bg = (iob * " + str(bg_sensitivity) \
                         + ") + " + str(bg_target) + " where iob_bg != (iob * " + str(bg_sensitivity) \
                         + ") + " + str(bg_target)
        self.cloop_db.execute(sql_set_iob_bg)


if __name__ == '__main__':
    logging.info("\nNEW Confirm Inj Process...")
    confirm = ConfirmInjectionProcess()
    confirm.run()
    logging.info("Done withConfirm Inj Process...\n\n")
