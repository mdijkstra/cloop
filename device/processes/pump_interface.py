# ############################################
#
# Edward Robinson
#
# Python script to sync the device db
# and the phone db. Sync via xml messages.
#
# ############################################

import signal
import subprocess
import time
import os
import logging
import datetime
import sys
import cloop_config

skip_commands = False  # debug tool to skip cli commands
windowsConfig = True
if "linux" not in sys.platform:
    windowsConfig = False

# use ISO format
dateFormat = "%Y-%m-%dT%H:%M:%S"
mySQLDateFormat = "%Y-%m-%d %H:%M:%S"

now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)

if windowsConfig:
    # device config
    logging.basicConfig(filename=currentDate + '.pump_interface.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # windows config
    logging.basicConfig(filename='./log/' + currentDate + '.pump_interface.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class PumpInterface():
    decoding_dir = "/home/pi/diabetes/decoding-carelink"
    output_file_default = "/tmp/"
    download_dir = "/tmp/"
    device_id = 584923
    port = "/dev/ttyUSB0"
    cur_page = None

    def __init__(self):
        pass

    def set_temp_basal(self, temp_rate=None, temp_duration=None, include_init=None):
        if temp_rate is None or temp_duration is None:
            logging.info("Temp rate or duration is null")
            return "ERRORCouldNotSetTempRate"
        logging.info("Going to set a temp_rate of "+str(temp_rate)+" for "+str(temp_duration)+" minutes.")

        if include_init is None:
            include_init = True

        # clean out previous file
        data_file = self.download_dir + "/set_temp_basal.txt"
        self.rm_file(data_file)

        # download cgm data
        command = "sudo"
        command += " " + self.decoding_dir + "/bin/mm-send-comm.py"
        if include_init:
            command += " --init"
        command += " --serial " + str(self.device_id)
        command += " --port " + self.port
        command += " --prefix-path " + self.download_dir
        command += " tweak ReadGlucoseHistory"
        command += " --save "
        for i in range(0, 2):
            result = self.cli_w_time(command=command)
            if result == 'ERRORTimeout':
                logging.warning("WARNING: command timeout. Trying to clean \
                         the stick buffer. On (" + str(i) + ") try")
                self.run_stick()
            elif not os.path.isfile(data_file):
                logging.warning("file not created on (" + str(i) + ") try. Running sticky...")
                self.run_stick()
            else:
                break

        if not os.path.isfile(data_file):
            logging.error("ERROR: Could not set temp rate")
            return "ERRORCouldNotSetTempRate"
        else:
            logging.info("INFO: Successfully set temp rate of "+str(temp_rate)+" for "+str(temp_duration)+" minutes.")
            return data_file

    def run_stick(self):
        logging.info("in run_stick")
        command = "sudo python"
        command += " " + self.decoding_dir + "/decocare/stick.py"
        command += " " + self.port
        result = self.cli_w_time(command=command, timeout=30)
        if result == 'ERRORTimeout':
            logging.error("WARNING: sticky command timeout.")
        logging.info("successfully ran sticky")

    def cli_w_time(self, command=None, timeout=60):
        """
    call shell-command and either return its output or kill it
    if it doesn't normally exit within timeout seconds and return None
    """
        logging.info("INFO: About to execute command (time " + str(timeout) + "): \n\t " + command)
        start = datetime.datetime.now()
        process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        out, err = process.communicate()
        while process.poll() is None:
            time.sleep(0.1)
            now = datetime.datetime.now()
            if (now - start).seconds > timeout:
                os.kill(process.pid, signal.SIGKILL)
                os.waitpid(-1, os.WNOHANG)
                logging.info("STDOUT: " + out)
                logging.info("STDERR: " + err)
                logging.error("Reached timeout")
                return 'ERRORTimeout'
        logging.info("STDOUT: " + out)
        logging.info("STDERR: " + err)
        logging.info("RETURN CODE: " + str(process.returncode))
        logging.info("ran command without timeout")
        return "SUCESSRanCommand"

    def rm_file(self, file_name):
        if skip_commands:
            return
        try:
            os.remove(file_name)
        except OSError:
            pass
