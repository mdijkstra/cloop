# ############################################
#
# Edward Robinson
#
# Python script to act as a db interface
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
    # windows config
    logging.basicConfig(filename=currentDate + '-injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # device config
    logging.basicConfig(filename="./log/" + currentDate + '-injection_process.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class CloopDB():
    db_host = "localhost"
    if windowsConfig:
        db_port = 33062  # windows config
    else:
        db_port = 3306  # device config
    db_user = "root"
    db_pass = "raspberry"
    db_db = "cloop"

    def __init__(self):
        self.db_conn = MySQLdb.connect(host=self.db_host,
                                       port=self.db_port,
                                       user=self.db_user,
                                       passwd=self.db_pass,
                                       db=self.db_db)
        self.db = self.db_conn.cursor()

    def __del__(self):
        self.db.close()
        self.db_conn.close()

    def select(self, sql):
        logging.info("SQL: " + sql)
        if sql[:6] != "select":
            return None
        self.db.execute(sql)
        return self.db.fetchall()

    def execute(self, sql):
        logging.info("SQL: " + sql)
        try:
            self.db.execute(sql)
            self.db_conn.commit()
        except:
            self.db_conn.rollback()

    def clear_db(self):
        self.execute("delete from courses_to_injections")
        self.execute("delete from injections")
        self.execute("delete from iob")
        self.execute("delete from courses")
        self.execute("delete from sgvs")
        self.execute("delete from automode_switch")
        self.execute("delete from alerts")

    def log(self, message_type, code, message):
        sql = "insert into logs (src_device, datetime_logged, code, type, message) values " \
              "('device', now(), '" + code + "','" + message_type + "','" + message + "')"
        self.execute(sql)