#############################################
#
# Edward Robinson
#
# Python script to sync the device db
# and the phone db. Sync via xml messages.
#
#############################################

import os
from bluetooth import *
from time import sleep



class DeviceBTPhoneTransData:
  uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
  phone_mac = "30:19:66:80:2F:B2"

  def __init__(self):
    self.open_con()

  def __del__(self):
    self.close_con()

  def transfer(self, xml_to_send):
    read_str = read()
    write(xml_to_send)
    return read_str

  def read(self, socket):
    try:
      while True:
        data = self.socket.recv(1024)
        if len(data) == 0: break
        print("DeviceBTPhoneTransData.read read : [%s]" % data)
        if "</EOM>" in data: break
    except IOError:
      pass
    return data

  def write(self, data_to_write):
    print "DeviceBTPhoneTransData.write writing to bt socket : "+data_to_write
    self.socket.send(data_to_write + "<EOM>")

  def open_con(self):
    service = bluetooth.find_service(address = phone_mac, uuid = uuid)
    if len(service) == 0:
      print "DeviceBTPhoneTransData.open_con ERROR: Couldn't find phone BT Service"
      return None
    self.socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    self.socket.connect((service[0]["host"], service[0]["port"]))
    print "DeviceBTPhoneTransData.open_con connecting to \"%s\" on %s" % (service[0]["name"], service[0]["host"])

  def close_con(self):
    self.socket.close()

# TODO: move get_value functions to a lib.py file
def get_value_from_xml (string, tag):
    start = string.index("<"+tag+">")
    end = string.index("</"+tag+">", start)
    return string[start+len(tag)+2:end]

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
    self.db_conn = MySQLdb.connect(host=db_host, port=db_port, user=db_user, passwd=db_pass, db=db_db)
    self.db = self.db_conn.cursor()  

  def __del__(self):
    self.db.close()
    self.db_conn.close()

  def get_data_to_sent(self):
    return self.export_sgvs()

  def import_data(self, xml):
    self.import_courses(xml)
    print "TODO: Import the various data elements from xml (currenlty just imports courses)"

  ################### Internal methods below ############################
  #switch from xml to json objects
  def import_sgvs(self, svgs_xml):
    print "TODO: Implement import_sgvs (" + svgs_xml + ")"
    print "      Should not be needed as the device sources the sgvs from the pump"
    print "      The API for importing data from the pump should probably be in a diff file"

  def export_sgvs(self):
    db.execute("select sgv_id, device_id, datetime_recorded, sgv from sgvs where transfered != 'yes'")
    xml = "<sgvs>"
    for row in db.fetchall():
      xml+= "<sgv_record>"
      xml += "<sgv_id>" + row[0] + "</sgv_id>"
      xml += "<device_id>" + row[1] + "</device_id>"
      xml += "<datetime_recorded>" + row[2] + "</datetime_recorded>"
      xml += "<sgv>" + row[3] + "</sgv>"
      xml += "</sgv_record>"
    xml += "</sgvs>"
    db.execute("update sgvs set transfered = 'yes'")
    return xml

  def import_courses(self, courses_xml):
    if courses_xml.index("<courses>") != 0:
      courses_xml = get_value_from_xml(courses_xml, "courses")
    courses = get_values_from_xml(courses_xml, "course")
    for course_xml in courses:
        insert_sql = course_xml_to_sql_insert(course_xml)
        try:
            # print "about to run : "+insert_sql
            self.db.execute(insert_sql)
            self.db_conn.commit()
        except:
            self.db_conn.rollback()
            print "******* rolled back insert : "+insert_sql


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



if __name__ == '__main__':
  db = DeviceDBTransData()
  data_to_send = db.get_data_to_send()
  bt_trans = DeviceBTPhoneTransData()
  data_from_phone = bt_trans.transfer(data_to_send)
  db.import_data(data_from_phone)  
