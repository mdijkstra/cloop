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
import time
import logging
import signal
import datetime
#from bluetooth import *
#from time import sleep

# use ISO format
dateFormat="%Y-%m-%dT%H:%M:%S"

now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)
logging.basicConfig(filename='./log/' + currentDate + '.log',level=logging.DEBUG,\
                format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class DeviceDBTransData():
  db_host = "localhost"
  db_port = 3306
  db_user = "root"
  db_pass = "raspberry"
  db_db = "cloop"
  cloop_config

  def __init__(self):
    self.db_conn = MySQLdb.connect(host=self.db_host, \
                    port=self.db_port, \
                    user=self.db_user, \
                    passwd=self.db_pass, \
                    db=self.db_db)
    self.db = self.db_conn.cursor()  
    self.cloop_config = CloopConfig();

  def __del__(self):
    self.db.close()
    self.db_conn.close()


  def process_injection(self):
    if self.temp_rate_in_action():
      exit
    temp_rate = self.get_injection_amount()
    attempts = 3
    successfully_executed = self.set_temp_basal(temp_rate, self.cloop_config.get_temp_duration(), attempts)
    if not successfully_executed:
      # log in bd and exit
    else
      # successful:
      self.set_injection_as_success()
      self.set_meals_as_covered()
      self.set_iob()
      self.log_injection_in_db()
      self.notify_injection()
      self.notify_time_to_eat()
      

  # return the amount insulin that should be injected
  # return null if no injection needed
  # based on carbs, IOB, current BG (if CGM accurate), carb senstivity, bg sensitivity
  def get_injection_ammount(self):
    cur_iob_units = self.get_cur_iob_units()
    cur_bg_units = self.get_cur_bg_units() #units over or under target bg
    carbs_to_cover = self.get_carbs_to_cover()
    units_for_carbs = self.carbs_to_cover_units()
    target_bg = self.cloop_config.get_target_bg()
    low_limit_units = self.cloop_config.get_low_limit_units()
    temp_duration = self.cloop_config.get_temp_duration()
    cur_basal_units = self.get_cur_basal_units(temp_duration)
    all_meal_carbs_absorbed = self.get_all_meal_carbs_absorbed()
    temp_rate = None
    correction_units = None 
    injection_units = None

    correction_units = cur_bg_units - cur_iob
    if all_meal_carbs_abosrbed and cur_bg_units > low_limit_units and cur_bg_units < cur_iob_units)
       correction_units = 0
    
    injection_units = units_for_carbs + correction_units
    temp_rate = ( cur_basal_units + injection_units ) / temp_duration
    # log all data into db
    # insert meals to injection records
    return temp_rate
      

  # get the current Insulin On Board
  def get_cur_iob_units(self):
    sql_select_iob = "select datetime_iob, iob from iob where datetime_iob = (select max(datetime_iob) from iob where datetime_iob < now() and datetime_iob > now()-10min)"
    self.db.execute(sql_select_sgvs)
    iob = 0 
    for row in self.db.fetchall():
      iob = row[1]
    return iob

  def get_cur_bg_units(self):

  def get_target_bg(self):

  def get_carbs_to_cover_units(self):

  def get_carbs_to_cover(self):

  def get_cur_basal_units(self):

  def get_all_meal_carbs_absorbed(self):

  def get_carb_sensitivity(self):
  
  def get_bg_sensitivity(self):

  def get_current_bg(self):
    return None

  def is_active_injection(self):


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
    logging.info('returning the following xml from export_sgvs:'+xml)
    return xml

  def export_sgvs_success(self):
    sql_update_exported = "update sgvs set transferred = 'yes' where transferred = 'no'" 
    logging.info('export_sgvs: updating transferred: '+sql_update_exported)
    try:
      self.db.execute(sql_update_exported)
      self.db_conn.commit()
    except:
      self.db_conn.rollback()
      logging.error("******* rolled back update : "+sql_update_exported)

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
    comment = get_value_from_xml(course_xml, "comment")
    # not needed on device, only needed on original location (app)
    #    transferred = get_value_from_xml(xml, "transferred")
    sql = " insert into courses (course_id, food_id, serv_quantity, carbs, \
            datetime_consumption, datetime_ideal_injection, comment) \
            values ( %d, %d, %f, %d, '%s','%s', '%s')" \
            % (int(course_id), int(food_id), float(serv_quantity), int(carbs), \
            datetime_consumption, datetime_ideal_injection, comment)

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
    logging.info('sucessfully transferred data from device to phone.')
    db_trans.export_sgvs_success()
    logging.info('main: going to import data from phone')
    db_trans.import_data(data_from_phone)  
  else:
    logging.warning('no data from phone')
  logging.info('DONE WITH PHONE SYNC\n\n\n\n')
