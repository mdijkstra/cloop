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
import bluetooth
import time
import logging
import signal
import datetime
# from bluetooth import *
# from time import sleep

# use ISO format
import sys
from cloop_config import CloopConfig

dateFormat = "%Y-%m-%dT%H:%M:%S"
now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)

windowsConfig = True
if "linux" not in sys.platform:
    windowsConfig = False

if windowsConfig:
    # device config
    logging.basicConfig(filename=currentDate + '.sync_device_phone.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # windows config
    logging.basicConfig(filename='./log/' + currentDate + '.sync_device_phone.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class DeviceBTPhoneTransData:
    uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
    phone_mac = "30:19:66:80:2F:B2"
    socket = None

    def __init__(self):
        self.open_con()

    def __del__(self):
        if not self.socket is None:
            self.close_con()

    def transfer(self, xml_to_send):
        logging.info("going to try to sync via BT")
        if self.socket is None:
            return None
        self.write(xml_to_send)
        time.sleep(2)
        read_str = self.read()
        logging.info("**summary of bt**")
        logging.info("wrote the following to bt  : " + xml_to_send)
        logging.info("read the following from bt : " + read_str)
        return read_str

    def read(self):
        data = None
        try:
            while True:
                data = self.socket.recv(1024)
                if len(data) == 0: break
                logging.info("DeviceBTPhoneTransData.read read : [%s]" % data)
                if "</EOM>" in data: break
        except IOError:
            pass
        return data

    def write(self, data_to_write):
        logging.info("DeviceBTPhoneTransData.write writing to bt socket : "
                     + data_to_write + "</EOM>")
        self.socket.send(data_to_write + "</EOM>")

    def open_con(self):
        service = bluetooth.find_service(address=self.phone_mac, uuid=self.uuid)
        if len(service) == 0:
            logging.error("DeviceBTPhoneTransData.open_con ERROR: Couldn't find phone BT Service")
            return None
        self.socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.socket.connect((service[0]["host"], service[0]["port"]))
        logging.info("DeviceBTPhoneTransData.open_con connecting to \"%s\" on %s"
                     % (service[0]["name"], service[0]["host"]))

    def close_con(self):
        self.socket.close()


# TODO: move get_value functions to a lib.py file
def get_value_from_xml(string, tag):
    start = string.index("<" + tag + ">") + len(tag) + 2
    end = string.index("</" + tag + ">", start)
    if start + 1 == end:
        return ""
    return string[start:end]


def get_values_from_xml(full_xml, tag):
    a = []
    while full_xml != "":
        a.append(get_value_from_xml(full_xml, tag))
        end_xml_tag = "</" + tag + ">"
        end_tag_loc = full_xml.index(end_xml_tag)
        full_xml = full_xml[end_tag_loc + len(end_xml_tag):]
    return a


def build_xml_record(outer_tag, inner_tags, values):
    xml = "<" + outer_tag + ">"
    for i in range(0, len(outer_tag), 1):
        xml += "<" + inner_tags[i] + ">" + str(values[i]) + "</" + inner_tags[i] + ">"
    return xml + "</" + outer_tag + ">"


# noinspection PyBroadException
class DeviceDBTransData():
    db_host = "localhost"
    if windowsConfig:
        db_port = 33062
    else:
        db_port = 3306
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

    def get_data_to_send(self):
        xml = self.export_sgvs()
        xml += self.export_iob()
        xml += self.export_injections()
        xml += self.export_log()
        xml += self.export_alerts()
        return xml

    def set_exports_successful(self):
        self.set_export_success("sgvs")
        self.set_export_success("iob")
        self.set_export_success("injections")
        self.set_export_success("log")
        self.set_export_success("alerts")

    def import_data(self, xml):
        self.import_courses(xml)
        logging.info("TODO: Import the various data elements from xml (currently just imports courses)")

    # ################## Internal methods below ############################
    # switch from xml to json objects
    def import_sgvs(self, svgs_xml):
        print "TODO: Implement import_sgvs (" + svgs_xml + ")"
        print "      Should not be needed as the device sources the sgvs from the pump"
        print "      The API for importing data from the pump should probably be in a diff file"

    def import_courses(self, courses_xml):
        if courses_xml.index("<courses>") == 0:
            courses_xml = get_value_from_xml(courses_xml, "courses")
        if courses_xml == "":
            return
        courses = get_values_from_xml(courses_xml, "course")
        for course_xml in courses:
            insert_sql = self.course_xml_to_sql_insert(course_xml)
            try:
                # print "about to run : "+insert_sql
                self.db.execute(insert_sql)
                self.db_conn.commit()
            except:
                self.db_conn.rollback()
                logging.error("******* rolled back insert : " + insert_sql)

    def set_export_success(self, table_name):
        logging.info("setting export successful for : "+table_name)
        sql_update = "update "+table_name+" set transferred = 'yes' where transferred = 'no'"
        self.db.execute(sql_update)
        self.db_conn.commit()

    def export_sgvs(self):
        sql_select_sgvs = "select sgv_id, device_id, datetime_recorded, sgv from sgvs where transferred = 'no'"
        logging.info('Exporting SGValues: ' + sql_select_sgvs)
        self.db.execute(sql_select_sgvs)
        xml = "<sgvs>"
        outer_tag = "sgv_record"
        inner_tags = ["sgv_id", "device_id", "datetime_recorded", "sgv"]
        for row in self.db.fetchall():
            values = [row[0], row[1], row[2].strftime(dateFormat), row[3]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</sgvs>"
        logging.info('returning the following xml from export_sgvs:' + xml)
        return xml

    def course_xml_to_sql_insert(self, course_xml):
        course_id = get_value_from_xml(course_xml, "course_id")
        food_id = get_value_from_xml(course_xml, "food_id")
        serv_quantity = get_value_from_xml(course_xml, "serv_quantity")
        carbs = get_value_from_xml(course_xml, "carbs")
        datetime_consumption = get_value_from_xml(course_xml, "datetime_consumption")
        datetime_ideal_injection = get_value_from_xml(course_xml, "datetime_ideal_injection")
        comment = get_value_from_xml(course_xml, "comment")
        # not needed on device, only needed on original location (app)
        #    transferred = get_value_from_xml(xml, "transferred")
        sql = " insert into courses (course_id, food_id, serv_quantity, carbs, \
            datetime_consumption, datetime_ideal_injection, comment) \
            values ( %d, %d, %f, %d, '%s','%s', '%s')" \
              % (int(course_id), int(food_id), float(serv_quantity), int(carbs),
                 datetime_consumption, datetime_ideal_injection, comment)

        logging.info(" courses_xml_to_sql_insert : " + sql)
        return sql

    def export_iob(self):
        sql_select = "select datetime_iob, iob from iob where transferred = 'no'"
        logging.info('Exporting iob: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<iobs>"
        outer_tag = "iob_record"
        inner_tags = ["datetime_iob", "iob"]
        for row in self.db.fetchall():
            values = [row[0], row[1].strftime(dateFormat)]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</iobs>"
        logging.info('returning the following xml from export_iob:' + xml)
        return xml

    def export_injections(self):
        sql_select = "select injection_id, units_intended, units_delivered, \
            temp_rate, datetime_intended, datetime_delivered, \
            cur_iob_units, cur_bg_units, correction_units, carbs_to_cover, carbs_units, \
            cur_basal_units, all_meal_carbs_absorbed, status \
            from injections where transferred = 'no'"
        logging.info('Exporting injections: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<injections>"
        outer_tag = "injection"
        inner_tags = ["injection_id", "units_intended", "units_delivered",
                      "temp_rate", "datetime_intended", "datetime_delivered",
                      "cur_iob_units", "cur_bg_units", "correction_units", "carbs_to_cover", "carbs_units",
                      "cur_basal_units", "all_meal_carbs_absorbed", "status"]
        for row in self.db.fetchall():
            values = [row[0], row[1], row[2],
                      row[3], row[4].strftime(dateFormat), row[5].strftime(dateFormat),
                      row[6], row[7], row[8], row[9], row[10],
                      row[11], row[12], row[13]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</injections>"
        logging.info('returning the following xml from export_injections:' + xml)
        return xml

    def export_log(self):
        sql_select = "select log_id, src_device, datetime_logged, code, type, message, option1, option2 \
            from log where transferred = 'no'"
        logging.info('Exporting log: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<logs>"
        outer_tag = "log"
        inner_tags = ["log_id", "src_device", "datetime_logged", "code",
                      "type", "message", "option1", "option2"]
        for row in self.db.fetchall():
            values = [row[0], row[1], row[2].strftime(dateFormat), row[3],
                      row[4], row[5], row[6], row[7]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</logs>"
        logging.info('returning the following xml from export_log:' + xml)
        return xml

    def export_alerts(self):
        sql_select = "select alert_id, datetime_recorded, datetime_to_alert, src, \
            code, type, message, value, option1, option2 \
            from alerts where transferred = 'no'"
        logging.info('Exporting alerts: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<alerts>"
        outer_tag = "alert"
        inner_tags = ["alert_id", "datetime_recorded", "datetime_to_alert", "src",
                      "code", "type", "message", "value", "option1", "option2"]
        for row in self.db.fetchall():
            values = [row[0], row[1].strftime(dateFormat), row[2].strftime(dateFormat), row[3],
                      row[4], row[5], row[6], row[7], row[8], row[9]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</alerts>"
        logging.info('returning the following xml from export_alerts:' + xml)
        return xml


'''
########## Test import courses with empty data #######
db_trans = DeviceDBTransData()
db_trans.import_courses("<courses></courses>")
'''

if __name__ == '__main__':
    cloop_config = CloopConfig()
    cloop_config.db_log("SUCCESS", "sync_device_phone", "Going to sync phone-device at "+str(now))
    logging.info('Going to try to sync device db and phone db...')
    db_trans = DeviceDBTransData()
    data_to_send = db_trans.get_data_to_send()
    bt_trans = DeviceBTPhoneTransData()
    data_from_phone = bt_trans.transfer(data_to_send)
    if not data_from_phone is None:
        logging.info('successfully transferred data from device to phone.')
        db_trans.set_exports_successful()
        logging.info('main: going to import data from phone')
        db_trans.import_data(data_from_phone)
    else:
        logging.warning('no data from phone')
    logging.info('DONE WITH PHONE SYNC\n\n\n\n')
    cloop_config.db_log("SUCCESS", "sync_device_phone", "Successfully completed phone-device sync at "+str(now))
