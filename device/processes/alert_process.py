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


class AlertProcess():
    cloop_db = cloop_db.CloopDB()
    cloop_config = cloop_config.CloopConfig()