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
#from bluetooth import *
#from time import sleep

dateFormat="%Y-%m-%dT%H:%M:%S"

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


class PumpDeviceDBTrans():
  db_host = "localhost"
  db_port = 3306
  db_user = "root"
  db_pass = "raspberry"
  db_db = "cloop"

  def __init__(self):
    self.db_conn = MySQLdb.connect(host=self.db_host, port=self.db_port, user=self.db_user, passwd=self.db_pass, db=self.db_db)
    self.db = self.db_conn.cursor()  

  def __del__(self):
    self.db.close()
    self.db_conn.close()

  def import_data(self, xml):
    self.import_courses(xml)
    print "TODO: Import the various data elements from xml (currenlty just imports courses)"

  ################### Internal methods below ############################
  #switch from xml to json objects
  def import_sgvs(self, svgs_xml):
    if sgvs_xml.startswith("<sgvs>"):
      sgvs_xml = get_value_from_xml(sgvs_xml, "sgvs")
    if not sgvs_xml == "":
      print "No SGVs to import. Done."
      return
    sgvs_array = get_values_from_xml(sgvs_xml, "sgv_record")
    for sgv in sgvs_array:
      self.import_sgv(sgv)

  def import_sgv(self, sgv_xml):
    device_id = get_value_from_xml(sgv_xml, "device_id")
    datetime_recorded = get_value_from_xml(sgv_xml, "datetime_recorded")
    sgv = get_value_from_xml(sgv_xml, "sgv")
    if not sgv_exists(datetime_recorded, device_id, sgv):
      sql = "insert into sgvs (device_id, datetime_recorded, sgv) values ("
      sql += device_id + ", "
      sql += "'" + datetime_recorded + "',"
      sql += sgv + ")"
      try:
        self.db.execute(sql) 
        self.db_conn.commit()
      except:
        self.db_conn.rollback()
        print "******* rolled back insert : "+insert_sql
        
  # TODO: Delete below, just keeping as reference for now
  def course_xml_to_sql_insert(self, course_xml):
    course_id = get_value_from_xml(course_xml, "course_id")
    food_id = get_value_from_xml(course_xml, "food_id")
    serv_quantity = get_value_from_xml(course_xml, "serv_quantity")
    carbs = get_value_from_xml(course_xml, "carbs")
    datetime_consumption = get_value_from_xml(course_xml, "datetime_consumption")
    datetime_ideal_injection = get_value_from_xml(course_xml, "datetime_ideal_injection")
    # not needed on device, only needed on original location (app)
    #    transfered = get_value_from_xml(xml, "transfered")
    sql = " insert into courses (course_id, food_id, serv_quantity, carbs, \
            datetime_consumption, datetime_ideal_injection) \
            values ( %d, %d, %f, %d, '%s','%s')" \
            % (int(course_id), int(food_id), float(serv_quantity), int(carbs), \
            datetime_consumption, datetime_ideal_injection)

    # print " SQL : "+sql
    return sql


class downloadPumpData():
  decoding_dir = "/home/pi/diabetes/decoding-carelink"
  output_file_default = "/tmp/pump_output.xml"
  cgm_download_file = "/tmp/cgm_download.data"
  device_id=584923
  port="/dev/ttyUSB0"
  cur_page=19

  def download_cgm_data(self, output_file=output_file_default):
    self.get_cur_cgm_page()
    

  def get_cur_cgm_page(self):
    return self.cur_page

if __name__ == '__main__':
  db_trans = DeviceDBTransData()
  data_to_send = db_trans.get_data_to_send()
  bt_trans = DeviceBTPhoneTransData()
  data_from_phone = bt_trans.transfer(data_to_send)
  if not data_from_phone is None:
    db_trans.import_data(data_from_phone)  



dt=$(date +%Y%m%d_%H%M%S)
sudo ./bin/mm-send-comm.py --init --prefix-path logs/$dt- --serial 584923 --port /dev/ttyUSB0 tweak ReadGlucoseHistory --page 18 --save | tee analysis/pg-18-$dt-ReadGlucoseHistory.markdown
sudo ./bin/mm-send-comm.py --prefix-path logs/$dt- --serial 584923 --port /dev/ttyUSB0 tweak ReadISIGHistory --page 18 --save | tee analysis/pg-18-$dt-ReadISIGHistory.markdown

