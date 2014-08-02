# ############################################
#
# Edward Robinson
#
# Python script to sync the device db
# and the phone db. Sync via xml messages.
#
# ############################################

import os
import MySQLdb
import time
import logging
import signal
import datetime
windowsConfig = True
# use ISO format
dateFormat = "%Y-%m-%dT%H:%M:%S"

now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)

if windowsConfig:
    # device config
    logging.basicConfig(filename=currentDate + '.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # windows config
    logging.basicConfig(filename='./log/' + currentDate + '.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class CloopConfig():
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

    def get_temp_duration(self):
        return 30

    def get_low_limit(self):
        return 100

    def get_low_limit_units(self):
        return (self.get_low_limit() - self.get_target_bg()) / self.get_bg_sensitivity()

    def get_target_bg(self):
        return 120

    def get_carb_sensitivity(self):
        return 13

    def get_bg_sensitivity(self):
        return 15


