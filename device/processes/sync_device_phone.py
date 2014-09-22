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
import cloop_db
# from bluetooth import *
# from time import sleep

# use ISO format
import sys
from cloop_config import CloopConfig

dateFormat = "%Y-%m-%dT%H:%M:%S"
now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)

windowsConfig = True
if "linux" in sys.platform:
    windowsConfig = False
else:
    windowsConfig = True

if windowsConfig:
    # windows config
    logging.basicConfig(filename=currentDate + '.sync_device_phone.log', level=logging.DEBUG,
                        format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')
else:
    # device config
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
            logging.error("Socket is None :(")
            return None
        self.write(xml_to_send)
        time.sleep(2)
        read_str = self.read()
        #logging.info("**summary of bt**")
        #logging.info("wrote the following to bt  : " + xml_to_send)
        #logging.info("read the following from bt : " + read_str)
        return read_str

    def read(self):
        data = ""
        try:
            while True:
                data += self.socket.recv(1024)
                if len(data) == 0:
                    break
                if "</EOM>" in data:
                    break
            #logging.info("DeviceBTPhoneTransData.read read : [%s]" % data)
        except IOError:
            return ""
        return data

    def write(self, data_to_write):
        logging.info("DeviceBTPhoneTransData.write writing to bt socket : "
                     + data_to_write + "</EOM>")
        self.socket.send(data_to_write + "</EOM>")

    def open_con(self):
        service = bluetooth.find_service(address=self.phone_mac, uuid=self.uuid)
        if len(service) == 0:
            logging.error("DeviceBTPhoneTransData.open_con ERROR: Couldn't find phone BT Service")
            self.socket = None
            return
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
    if start >= end:
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
    for i in range(0, len(inner_tags), 1):
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
        self.set_export_success("logs")
        self.set_export_success("alerts")

    def import_data(self, xml):
        self.import_courses(xml)
        self.import_halts(xml)
        self.import_automodes(xml)
        logging.info("TODO: Import the various data elements from xml (currently just imports courses)")

    # ################## Internal methods below ############################
    # switch from xml to json objects
    def import_sgvs(self, svgs_xml):
        print "TODO: Implement import_sgvs (" + svgs_xml + ")"
        print "      Should not be needed as the device sources the sgvs from the pump"
        print "      The API for importing data from the pump should probably be in a diff file"

    def import_courses(self, courses_xml):
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

    def import_halts(self, halts_xml):
        halts_xml = get_value_from_xml(halts_xml, "halts")
        if halts_xml == "":
            return
        halts = get_values_from_xml(halts_xml, "halt")
        for halt_xml in halts:
            insert_sql = self.halt_xml_to_sql_insert(halt_xml)
            try:
                # print "about to run : "+insert_sql
                self.db.execute(insert_sql)
                self.db_conn.commit()
            except:
                self.db_conn.rollback()
                logging.error("******* rolled back insert : " + insert_sql)

    def import_automodes(self, automodes_xml):
        automodes_xml = get_value_from_xml(automodes_xml, "automodes")
        if automodes_xml == "":
            return
        automodes = get_values_from_xml(automodes_xml, "automode")
        for automode_xml in automodes:
            insert_sql = self.automode_xml_to_sql_insert(automode_xml)
            try:
                # print "about to run : "+insert_sql
                self.db.execute(insert_sql)
                self.db_conn.commit()
            except:
                self.db_conn.rollback()
                logging.error("******* rolled back insert : " + insert_sql)

    def set_export_success(self, table_name):
        logging.info("setting export successful for : " + table_name)
        sql_update = "update " + table_name + " set transferred = 'yes' where transferred = 'transferring'"
        self.db.execute(sql_update)
        self.db_conn.commit()

    def export_sgvs(self):
        sql_set_transferring = "update sgvs set transferred = 'transferring' " \
                               "where transferred = 'transferring' or transferred = 'no' " \
                               "order by datetime_recorded desc limit 10"
        self.db.execute(sql_set_transferring)
        self.db_conn.commit()
        sql_select_sgvs = "select sgv_id, device_id, datetime_recorded, sgv from sgvs " \
                          "where transferred = 'transferring'"
        logging.info('Exporting SGValues: ' + sql_select_sgvs)
        self.db.execute(sql_select_sgvs)
        xml = "<sgvs>"
        outer_tag = "sgv_record"
        inner_tags = ["sgv_id", "device_id", "datetime_recorded", "sgv"]
        for row in self.db.fetchall():
            values = [row[0], row[1], self.date_or_null(row[2]), row[3]]
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
        # transferred = get_value_from_xml(xml, "transferred")
        sql = " insert into courses (course_id, food_id, serv_quantity, carbs, \
            datetime_consumption, datetime_ideal_injection, comment) \
            values ( %d, %d, %f, %d, '%s','%s', '%s')" \
              % (int(course_id), int(food_id), float(serv_quantity), int(carbs),
                 datetime_consumption, datetime_ideal_injection, comment)

        logging.info(" courses_xml_to_sql_insert : " + sql)
        return sql

    def export_iob(self):
        sql_set_transferring = "update iob set transferred = 'transferring' " \
                               "where transferred = 'transferring' or transferred = 'no' " \
                               "order by datetime_iob limit 10"
        self.db.execute(sql_set_transferring)
        self.db_conn.commit()
        sql_select = "select datetime_iob, iob, iob_bg from iob where transferred = 'transferring'"
        logging.info('Exporting iob: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<iobs>"
        outer_tag = "iob_record"
        inner_tags = ["datetime_iob", "iob", "iob_bg"]
        for row in self.db.fetchall():
            values = [self.date_or_null(row[0]), row[1], row[2]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</iobs>"
        logging.info('returning the following xml from export_iob:' + xml)
        return xml

    def export_injections(self):
        sql_set_transferring = "update injections set transferred = 'transferring' " \
                               "where transferred = 'transferring' or transferred = 'no' " \
                               "order by datetime_intended limit 10"
        self.db.execute(sql_set_transferring)
        self.db_conn.commit()
        sql_select = "select injection_id, units_intended, units_delivered, \
            temp_rate, datetime_intended, datetime_delivered, \
            cur_iob_units, cur_bg_units, correction_units, carbs_to_cover, carbs_units, \
            cur_basal_units, all_meal_carbs_absorbed, status \
            from injections where transferred = 'transferring'"
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
                      row[3], self.date_or_null(row[4]), self.date_or_null(row[5]),
                      row[6], row[7], row[8], row[9], row[10],
                      row[11], row[12], row[13]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</injections>"
        logging.info('returning the following xml from export_injections:' + xml)
        return xml

    def export_log(self):
        sql_set_transferring = "update logs set transferred = 'transferring' " \
                               "where transferred = 'transferring' or transferred = 'no' " \
                               "order by datetime_logged limit 10"
        self.db.execute(sql_set_transferring)
        self.db_conn.commit()
        sql_select = "select log_id, src_device, datetime_logged, code, type, message, option1, option2 \
            from logs where transferred = 'transferring'"
        logging.info('Exporting log: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<logs>"
        outer_tag = "log"
        inner_tags = ["log_id", "src_device", "datetime_logged", "code",
                      "type", "message", "option1", "option2"]
        for row in self.db.fetchall():
            values = [row[0], row[1], self.date_or_null(row[2]), row[3],
                      row[4], row[5], row[6], row[7]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</logs>"
        logging.info('returning the following xml from export_log:' + xml)
        return xml

    def export_alerts(self):
        sql_set_transferring = "update alerts set transferred = 'transferring' " \
                               "where transferred = 'transferring' or transferred = 'no' " \
                               "order by datetime_recorded limit 10"
        self.db.execute(sql_set_transferring)
        self.db_conn.commit()
        sql_select = "select alert_id, datetime_recorded, datetime_to_alert, src, \
            code, type, title, message, value, option1, option2 \
            from alerts where transferred = 'transferring'"
        logging.info('Exporting alerts: ' + sql_select)
        self.db.execute(sql_select)
        xml = "<alerts>"
        outer_tag = "alert"
        inner_tags = ["alert_id", "datetime_recorded", "datetime_to_alert", "src",
                      "code", "type", "title", "message", "value", "option1", "option2"]
        for row in self.db.fetchall():
            values = [row[0], self.date_or_null(row[1]), self.date_or_null(row[2]), row[3],
                      row[4], row[5], row[6], row[7], row[8], row[9], row[10]]
            xml += build_xml_record(outer_tag, inner_tags, values)
        xml += "</alerts>"
        logging.info('returning the following xml from export_alerts:' + xml)
        return xml

    def date_or_null(self, dt):
        if dt is None:
            return "null"
        else:
            return dt.strftime(dateFormat)

    def automode_xml_to_sql_insert(self, automode_xml):
        automode_id = get_value_from_xml(automode_xml, "automode_switch_id")
        datetime_recorded = get_value_from_xml(automode_xml, "datetime_recorded")
        is_on = get_value_from_xml(automode_xml, "is_on")
        sql = " insert into automode_switch (automode_switch_id, datetime_recorded, is_on)" \
              + " values ( %d, '%s','%s')" \
                % (int(automode_id), datetime_recorded, is_on)
        logging.info(" courses_xml_to_sql_insert : " + sql)
        return sql

    def halt_xml_to_sql_insert(self, halt_xml):
        halt_id = get_value_from_xml(halt_xml, "halt_id")
        datetime_issued = get_value_from_xml(halt_xml, "datetime_issued")
        sql = " insert into halts (halt_id, datetime_issued)" \
              + " values ( %d, '%s')" \
                % (int(halt_id), datetime_issued)
        logging.info(" courses_xml_to_sql_insert : " + sql)
        return sql


'''
########## Test import courses with empty data #######
db_trans = DeviceDBTransData()
db_trans.import_courses("<courses></courses>")
'''

if __name__ == '__main__':
    cloop_config = CloopConfig()
    # cloop_config.db_log("SUCCESS", "sync_device_phone", "Going to sync phone-device at "+str(now))
    logging.info('Going to try to sync device db and phone db...')
    db_trans = DeviceDBTransData()
    data_to_send = db_trans.get_data_to_send()
    cloop_db = cloop_db.CloopDB()
    bt_trans = DeviceBTPhoneTransData()
    data_from_phone = bt_trans.transfer(data_to_send)
    if data_from_phone is not None:
        logging.info('successfully transferred data from device to phone.')
        db_trans.set_exports_successful()
        logging.info('main: going to import data from phone')
        db_trans.import_data(data_from_phone)
    else:
        logging.warning('no data from phone')
        cloop_db.log("FAIL", "sync_device_phone", "Failed to sync phone-device at " + str(now))
    logging.info('DONE WITH PHONE SYNC\n\n\n\n')
    cloop_db.log("SUCCESS", "sync_device_phone", "Successfully completed phone-device sync at " + str(now))
