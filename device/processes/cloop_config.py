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
import sys

if "linux" in sys.platform:
    windowsConfig = False
else:
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

    def get_bg_sensitivity(self, datetime=None):
        return 30

    def get_cur_basal_units(self, duration=None):
        if duration is None:
            duration = self.get_temp_duration()
        return 1.1 * (duration / 60)