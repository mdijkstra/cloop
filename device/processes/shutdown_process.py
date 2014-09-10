# ############################################
#
# Edward Robinson
#
# Python script to shutdown if issued.
#
# ############################################

import os
import MySQLdb
import time
import logging
import signal
import datetime
import sys
import cloop_config
import cloop_db

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


class Shutdown():
    def __init__(self):
        self.cloop_config = cloop_config.CloopConfig()
        self.cloop_db = cloop_db.CloopDB()

    def run(self):
        if not self.is_shutdown_needed():
            return
        self.cloop_db.log("INFO", "halt_process", "Going to halt at "+str(currentDate))
        self.update_halts()
        self.shutdown()

    def shutdown(self):
        if not windowsConfig:
            os.system("halt")

    def is_shutdown_needed(self):
        rows = self.cloop_db.select("select * from halts where status = 'no'")
        if len(rows) > 0:
            return True
        else:
            return False

    def update_halts(self):
        self.cloop_db.execute("update halts set status = 'yes'")

if __name__ == '__main__':
    logging.info("\n\n\nShutdown process started")
    process = Shutdown()
    process.run()
    logging.info("Shutdown process ended\n")