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
        self.cloop_config = cloop_config.CloopConfig()

    def __del__(self):
        self.db.close()
        self.db_conn.close()

    def run(self):
        if not self.is_shutdown_needed():
            return
        self.cloop_config.db_log("INFO", "halt_process", "Going to halt at "+str(currentDate))
        self.update_halts()
        self.shutdown()

    def shutdown(self):
        if not windowsConfig:
            os.system("halt")

    def is_shutdown_needed(self):
        self.db.execute("select * from halts where status = 'no'")
        rows = self.db.fetchall()
        if len(rows) >= 0:
            return True
        else:
            return False

    def update_halts(self):
        self.db.execute("update halts set status = 'yes'")
        self.db_conn.commit()

if __name__ == '__main__':
    logging.info("\n\n\nShutdown process started")
    process = Shutdown()
    process.run()
    logging.info("Shutdown process ended\n")

