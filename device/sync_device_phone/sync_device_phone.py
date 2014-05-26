#############################################
#
# Edward Robinson
#
# Python script to sync the device db
# and the phone db. Sync via xml messages.
#
#############################################

import os
import MySQLdb
import bluetooth
import time
import logging
import signal
#from bluetooth import *
#from time import sleep

# use ISO format
dateFormat="%Y-%m-%dT%H:%M:%S"

logging.basicConfig(filename='sync_device_phone.log',level=logging.DEBUG,\
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
    logging.info("DeviceBTPhoneTransData.write writing to bt socket : "\
                    +data_to_write + "</EOM>")
    self.socket.send(data_to_write + "</EOM>")

  def open_con(self):
    service = bluetooth.find_service(address = self.phone_mac, uuid = self.uuid)
    if len(service) == 0:
      logging.error("DeviceBTPhoneTransData.open_con ERROR: Couldn't find phone BT Service")
      return None
    self.socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    self.socket.connect((service[0]["host"], service[0]["port"]))
    logging.info("DeviceBTPhoneTransData.open_con connecting to \"%s\" on %s" \
                    % (service[0]["name"], service[0]["host"]))

  def close_con(self):
    self.socket.close()

# TODO: move get_value functions to a lib.py file
def get_value_from_xml (string, tag):
    start = string.index("<"+tag+">") + len(tag) + 2
    end = string.index("</"+tag+">", start)
    if start + 1 == end:
      return ""
    return string[start:end]

def get_values_from_xml (full_xml, tag):
    a = []
    i = 0
    while full_xml != "":
        a.append(get_value_from_xml(full_xml, tag))
        end_xml_tag = "</"+tag+">"
        end_tag_loc = full_xml.index(end_xml_tag)
        full_xml = full_xml[end_tag_loc+len(end_xml_tag):]
    return a


class DeviceDBTransData():
  db_host = "localhost"
  db_port = 3306
  db_user = "root"
  db_pass = "raspberry"
  db_db = "cloop"

  def __init__(self):
    self.db_conn = MySQLdb.connect(host=self.db_host, \
                    port=self.db_port, \
                    user=self.db_user, \
                    passwd=self.db_pass, \
                    db=self.db_db)
    self.db = self.db_conn.cursor()  

  def __del__(self):
    self.db.close()
    self.db_conn.close()

  def get_data_to_send(self):
    return self.export_sgvs()

  def import_data(self, xml):
    self.import_courses(xml)
    logging.info("TODO: Import the various data elements from xml (currenlty just imports courses)")

  ################### Internal methods below ############################
  #switch from xml to json objects
  def import_sgvs(self, svgs_xml):
    print "TODO: Implement import_sgvs (" + svgs_xml + ")"
    print "      Should not be needed as the device sources the sgvs from the pump"
    print "      The API for importing data from the pump should probably be in a diff file"

  def export_sgvs(self):
    sql_select_sgvs = "select sgv_id, device_id, datetime_recorded, sgv from sgvs where transferred != 'yes'"
    logging.info('Exporting SGValues: '+ sql_select_sgvs)
    self.db.execute(sql_select_sgvs)
    xml = "<sgvs>"
    for row in self.db.fetchall():
      xml+= "<sgv_record>"
      xml += "<sgv_id>" + str(row[0]) + "</sgv_id>"
      xml += "<device_id>" + str(row[1]) + "</device_id>"
      xml += "<datetime_recorded>" + row[2].strftime(dateFormat) + "</datetime_recorded>"
      xml += "<sgv>" + str(row[3]) + "</sgv>"
      xml += "</sgv_record>"
    xml += "</sgvs>"
    sql_update_exported = "update sgvs set transferred = 'yes' where transferred = 'no'" 
    logging.info('export_sgvs: updating transferred: '+sql_update_exported)
    try:
      self.db.execute(sql_update_exported)
      self.db_conn.commit()
    except:
      self.db_conn.rollback()
      logging.error("******* rolled back update : "+sql_update_exported)
    logging.info('returning the following xml from export_sgvs:'+xml)
    return xml

  def import_courses(self, courses_xml):
    index = courses_xml.index("<courses>")
    if courses_xml.index("<courses>") == 0:
      courses_xml = get_value_from_xml(courses_xml, "courses")
    if courses_xml == "":
      return
    courses = get_values_from_xml(courses_xml, "course")
    for course_xml in courses:
        insert_sql = course_xml_to_sql_insert(course_xml)
        try:
            # print "about to run : "+insert_sql
            self.db.execute(insert_sql)
            self.db_conn.commit()
        except:
            self.db_conn.rollback()
            logging.error("******* rolled back insert : "+insert_sql)


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
            % (int(course_id), int(food_id), float(serv_quantity), int(carbs), \
            datetime_consumption, datetime_ideal_injection)

    logging.info(" courses_xml_to_sql_insert : "+sql)
    return sql

'''
########## Test import courses with empty data #######
db_trans = DeviceDBTransData()
db_trans.import_courses("<courses></courses>")
'''


if __name__ == '__main__':
  logging.info('Going to try to sync device db and phone db...')
  db_trans = DeviceDBTransData()
  data_to_send = db_trans.get_data_to_send()
  bt_trans = DeviceBTPhoneTransData()
  data_from_phone = bt_trans.transfer(data_to_send)
  if not data_from_phone is None:
    logging.info('main: going to import data from phone')
    db_trans.import_data(data_from_phone)  
  else:
    logging.warning('no data from phone')
  logging.info('DONE WITH PHONE SYNC\n\n\n\n')
