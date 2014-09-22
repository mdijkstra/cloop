# ############################################
#
# Edward Robinson
#
# Python script to read data off the pump
# and load it into the device db. 
# Sync via xml messages.
#
#############################################

import os
import MySQLdb
import time
import sys
import datetime
import logging
import signal
import subprocess
#from bluetooth import *
#from time import sleep
from cloop_config import CloopConfig
import cloop_db

dateFormat = "%Y-%m-%dT%H:%M:%S"
mySQLDateFormat = "%Y-%m-%d %H:%M:%S"
skip_commands = False  # debug tool to skip cli commands

now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)
logging.basicConfig(filename='./log/' + currentDate + '-sync-pump.log', level=logging.DEBUG,
                    format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s ')


# TODO: move get_value functions to a lib.py file
def get_value_from_xml(string, tag):
    start = string.index("<" + tag + ">") + len(tag) + 2
    end = string.index("</" + tag + ">", start)
    if start >= end:
        return ""
    return string[start:end]


def get_values_from_xml(full_xml, tag):
    a = []
    i = 0
    while full_xml != "":
        a.append(get_value_from_xml(full_xml, tag))
        end_xml_tag = "</" + tag + ">"
        end_tag_loc = full_xml.index(end_xml_tag)
        full_xml = full_xml[end_tag_loc + len(end_xml_tag):]
    return a


# TODO: move to library file
class Timeout():
    """
  Timeout class using ALARM signal
  http://pythonadventures.wordpress.com/2012/12/08/raise-a-timeout-exception-after-x-seconds/
  """

    class Timeout(Exception):
        pass

    def __init__(self, sec):
        self.sec = sec

    def __enter__(self):
        signal.signal(signal.SIGALRM, self.raise_timeout)
        signal.alarm(self.sec)

    def __exit__(self, *args):
        signal.alarm(0)  # disable alarm

    def raise_timeout(self, *args):
        raise Timeout.Timeout()


class PumpDeviceDBTrans():
    db_host = "localhost"
    db_port = 3306
    db_user = "root"
    db_pass = "raspberry"
    db_db = "cloop"

    def __init__(self):
        self.db_conn = MySQLdb.connect(host=self.db_host, port=self.db_port, user=self.db_user, passwd=self.db_pass,
                                       db=self.db_db)
        self.db = self.db_conn.cursor()

    def __del__(self):
        self.db.close()
        self.db_conn.close()

    ################### Internal methods below ############################
    # TODO: possibly switch from xml to json objects
    def import_sgvs(self, sgvs_xml):
        logging.debug('importing sgvs xml : ' + sgvs_xml)
        if sgvs_xml.startswith("<sgvs>"):
            sgvs_xml = get_value_from_xml(sgvs_xml, "sgvs")
        if sgvs_xml == "":
            logging.debug('No SGVs to import. Done.')
            return
        sgvs_array = get_values_from_xml(sgvs_xml, "sgv_record")
        for sgv in sgvs_array:
            self.import_sgv(sgv)

    def import_sgv(self, sgv_xml):
        logging.debug('importing sgv : ' + sgv_xml)
        device_id = get_value_from_xml(sgv_xml, "device_id")
        datetime_recorded = get_value_from_xml(sgv_xml, "datetime_recorded")
        sgv = get_value_from_xml(sgv_xml, "sgv")
        sql = "insert into sgvs (device_id, datetime_recorded, sgv, transferred) select * from (select "
        sql += device_id + ", "
        sql += "'" + datetime_recorded + "',"
        sql += sgv + ", 'no')"
        sql += " as tmp where not exists (select sgv from sgvs where "
        sql += " device_id = " + device_id
        sql += " and datetime_recorded = '" + datetime_recorded + "'"
        sql += ") limit 1"
        try:
            #logging.debug("Running sql '" + sql + "'")
            self.db.execute(sql)
            self.db_conn.commit()
        except:
            self.db_conn.rollback()
            logging.error('importing sgv : ' + sgv_xml)
            logging.error("******* rolled back insert : " + sql)

    # TODO: Delete below, just keeping as reference for now
    def course_xml_to_sql_insert(self, course_xml):
        course_id = get_value_from_xml(course_xml, "course_id")
        food_id = get_value_from_xml(course_xml, "food_id")
        serv_quantity = get_value_from_xml(course_xml, "serv_quantity")
        carbs = get_value_from_xml(course_xml, "carbs")
        datetime_consumption = get_value_from_xml(course_xml, "datetime_consumption")
        datetime_ideal_injection = get_value_from_xml(course_xml, "datetime_ideal_injection")
        # not needed on device, only needed on original location (app)
        #    transferred = get_value_from_xml(xml, "transferred")
        sql = " insert into courses (course_id, food_id, serv_quantity, carbs, \
            datetime_consumption, datetime_ideal_injection) \
            values ( %d, %d, %f, %d, '%s','%s')" \
              % (int(course_id), int(food_id), float(serv_quantity), int(carbs),
                 datetime_consumption, datetime_ideal_injection)

        print " SQL : " + sql
        return sql


class DownloadPumpData():
    decoding_dir = "/home/pi/diabetes/decoding-carelink"
    output_file_default = "/tmp/"
    cgm_download_dir = "/tmp/"
    device_id = 584923
    port = "/dev/ttyUSB0"
    cur_page = None


    def get_latest_sgv(self):
        page_data_file = self.download_cgm_data()
        if page_data_file != 'ERRORCouldNotDownload':
            bytes = self.file_to_bytes(page_data_file)
            last_sgv = self.get_last_sgv_from_bytes(bytes)
            return self.sgv_to_xml(last_sgv)
        else:
            return

    def download_cgm_data(self, page_num=None, include_init=True):
        logging.info("Going to try to download CGM data from the pump")
        # if didn't pass a page get the current
        self.cur_page = None

        if page_num is None:
            self.get_cur_cgm_page(include_init=True)
            include_init = False
        else:
            self.cur_page = page_num

        if self.cur_page is None:
            logging.error("Could not get the CGM page to download. Returning")
            return 'ERRORNoPage'
        # clean out previous file
        data_file = self.cgm_download_dir + "/ReadGlucoseHistory-page-" + str(self.cur_page) + ".data"
        self.rm_file(data_file)

        # download cgm data
        command = "sudo"
        command += " " + self.decoding_dir + "/bin/mm-send-comm.py"
        if include_init:
            command += " --init"
        command += " --serial " + str(self.device_id)
        command += " --port " + self.port
        command += " --prefix-path " + self.cgm_download_dir
        command += " tweak ReadGlucoseHistory"
        command += " --page " + str(self.cur_page)
        command += " --save "
        for i in range(0, 2):
            result = self.cli_w_time(command=command)
            if result != 'Successful':
                logging.warning("WARNING: command failed. Trying to clean \
                         the stick buffer. On (" + str(i) + ") try")
                self.run_stick()
            elif not os.path.isfile(data_file):
                logging.warning("file not created on (" + str(i) + ") try. Running sticky...")
                self.run_stick()
            else:
                break

        if not os.path.isfile(data_file):
            logging.error("ERROR: Could not download cgm page data")
            return "ERRORCouldNotDownload"
        else:
            logging.info("INFO: Sucessfully downloaded cgm page data")
            return data_file

    def cgm_data_file_to_sgv_xml(self, file_name):
        logging.info("Going to try to convert the cgm data file (" + file_name + ")")
        sys.path.insert(0, self.decoding_dir)
        import list_cgm
        from list_cgm import PagedData

        records = []
        if not os.path.isfile(file_name):
            logging.error("file does not exist to decode (" + file_name + ")")
            return 'ERRORFileDoesNotExist'
        with open(file_name, 'rb') as stream:
            #      for stream in file_name:
            page = PagedData(stream)
            records.extend(page.decode())

        # convert the records to sgv xml
        xml = "<sgvs>"
        for record in records:
            if record['name'] == 'GlucoseSensorData':
                xml += self.sgv_to_xml(record['sgv'], record['date'])
        xml += "</sgvs>"
        logging.info("sucessfully decoded cgm data file : " + xml)
        return xml

    def run_stick(self):
        logging.info("in run_stick")
        command = "sudo python"
        command += " " + self.decoding_dir + "/decocare/stick.py"
        command += " " + self.port
        result = self.cli_w_time(command=command, timeout=30)
        if result != 'Successful':
            logging.error("WARNING: sticky command failed.")
        else:
            logging.info("successfully ran sticky")

    def get_cur_cgm_page(self, include_init=True):
        logging.info("going to try to download the current cgm page")
        # clear the previous download file
        download_file = self.cgm_download_dir + "/ReadCurGlucosePageNumber.data"
        self.rm_file(download_file)

        # download the page from the pump
        command = "sudo"
        command += " " + self.decoding_dir + "/bin/mm-send-comm.py"
        if include_init:
            command += " --init"
        command += " --serial " + str(self.device_id)
        command += " --port " + self.port
        command += " --prefix-path " + self.cgm_download_dir
        command += " --prefix ReadCurGlucosePageNumber"
        command += " --save "
        command += " sleep 0"

        for i in range(0, 2):
            result = self.cli_w_time(command=command)
            if result != 'Successful':
                logging.warning("WARNING: command failed. Trying to clean \
                         the stick buffer. On (" + str(i) + ") try")
                self.run_stick()
            elif not os.path.isfile(download_file):
                logging.warning('file not downloaded. on (' + str(i) + ') try')
                self.run_stick()
            else:
                break
        if not os.path.isfile(download_file):
            logging.error("ERROR: Current Page was not downloaded")
            return 'ERRORNotDownloaded'

        # decode the page number
        cur_page_data = self.file_to_bytes(download_file)
        # array style count
        cur_page = int(cur_page_data[5]) - 1
        if cur_page < 0 or cur_page > 500:
            logging.error("ERROR: Could not decode the current \
                     page (" + str(cur_page) + ")")
            return 'ERRORCouldNotParse'
        logging.info("INFO: Found the current page to be '" + str(cur_page) + "'")
        self.cur_page = cur_page
        return self.cur_page

    def cli(self, command):
        if not skip_commands:
            logging.info("INFO: About to execute command: \n\t " + command)
            os.system(command)

    def cli_w_time(self, command=None, timeout=60):
        """
    call shell-command and either return its output or kill it
    if it doesn't normally exit within timeout seconds and return None
    """
        logging.info("INFO: About to execute command (time " + str(timeout) \
                     + "): \n\t " + command)
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
        if process.returncode != 0:
            return "ERRORCode-"+str(process.returncode)
        else:
            return "Successful"

    def file_to_bytes(self, file_name):
        logging.debug("file_to_bytes (" + file_name + ")")
        myBytes = bytearray()
        with open(file_name, 'rb') as file:
            while 1:
                byte = file.read(1)
                if not byte:
                    break
                myBytes.append(byte)
        return myBytes

    def rm_file(self, file_name):
        if skip_commands:
            return
        try:
            os.remove(file_name)
        except OSError:
            pass

    def get_last_sgv_from_bytes(self, bytes):
        latest_sg = 0
        # for each byte: convert it to a decimal, double it
        # 	check that it is a valid sg and then mark it as the latest
        for i in range(0, len(bytes) - 10):
            bin = '{0:08b}'.format(bytes[i])
            hex = '{0:02x}'.format(bytes[i])
            dec = int(hex, 16)
            sg = dec * 2
            if sg == 0:
                numZerosCounter = numZerosCounter + 1
            else:
                numZerosCounter = 0
            if numZerosCounter > 20:
                break
            if sg > 40 and sg < 400:
                latest_sg = sg
        return latest_sg

    def sgv_to_xml(self, sgv, date_isoformat):
        xml = "<sgv_record>"
        xml += "<device_id>" + str(self.device_id) + "</device_id>"
        #    xml += "<datetime_recorded>" + datetime.datetime.now().strftime(mySQLDateFormat) + "</datetime_recorded>"
        xml += "<datetime_recorded>" + date_isoformat + "</datetime_recorded>"
        xml += "<sgv>" + str(sgv) + "</sgv>"
        xml += "</sgv_record>"
        return xml


if __name__ == '__main__':
    # downlaod the data from the pump
    # parse it to get the latest sgv
    logging.info("\n\nNew pump sync\n")
    cloop_config = CloopConfig()
    db = cloop_db.CloopDB()
    db.log("SUCCESS", "sync_device_pump", "Going to sync device-pump at "+str(now))
    logging.info("NEW SYNC...")
    download_pump = DownloadPumpData()
    file_output = download_pump.download_cgm_data()
    cgm_xml = download_pump.cgm_data_file_to_sgv_xml(file_output)
    if cgm_xml == 'ERRORFileDoesNotExist':
        db.log("FAIL", "sync_device_pump", "Failed to sync device-pump at "+str(now))
        logging.error('Could not complete sync\n\n\n\n')
    else:
        #  last_sgv_xml = download_pump.get_latest_sgv()
        # import that sgv into the db
        db_trans = PumpDeviceDBTrans()
        #  db_trans.import_sgv(latest_sgv_xml)
        db_trans.import_sgvs(cgm_xml)
        db.log("SUCCESS", "sync_device_pump", "Successfully synced device-pump sgvs at "+str(now))
        logging.info("\nDONE WITH SYNC.\n\n\n\n")






#  db_trans = DeviceDBTransData()
#  data_to_send = db_trans.get_data_to_send()
#  bt_trans = DeviceBTPhoneTransData()
#  data_from_phone = bt_trans.transfer(data_to_send)
#  if not data_from_phone is None:
#    db_trans.import_data(data_from_phone)  

#sudo ./bin/mm-send-comm.py --prefix-path logs/$dt- --serial 584923 --port /dev/ttyUSB0 tweak ReadISIGHistory --page 18 --save | tee analysis/pg-18-$dt-ReadISIGHistory.markdown

#fileInName = "20140421_030133-ReadGlucoseHistory-page-16.data"
#fileInName = "20140421_042530-ReadGlucoseHistory-page-0.data"
#fileOutName = "latest-sg.xml"
#j = 0
#fileOut = open(fileOutName, 'w')
#numZerosCounter = 0
#fileOut.write("<latest_sg>"+str(latest_sg)+"</latest_sg>")

