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
import cloop_config
#from bluetooth import *
#from time import sleep

# use ISO format
dateFormat="%Y-%m-%dT%H:%M:%S"

now = datetime.datetime.now()
currentDate = str(now.year) + "-" + str(now.month) + "-" + str(now.day)
logging.basicConfig(filename='./log/' + currentDate + '.log',level=logging.DEBUG,\
                format='%(asctime)s %(levelname)s at %(lineno)s: %(message)s')


class injection_process():
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
    self.cloop_config = cloop_config();

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
    units_for_carbs, courses_to_cover = self.carbs_to_cover_units()
    low_limit_units = self.cloop_config.get_low_limit_units()
    temp_duration = self.cloop_config.get_temp_duration()
    cur_basal_units = self.get_cur_basal_units(temp_duration)
    all_meal_carbs_absorbed = self.get_all_meal_carbs_absorbed()
    temp_rate = None
    correction_units = None 
    injection_units = None

    correction_units = cur_bg_units - cur_iob
    if all_meal_carbs_abosrbed \
                    and cur_bg_units > low_limit_units \
                    and cur_bg_units < cur_iob_units:
       correction_units = 0
    
    injection_units = units_for_carbs + correction_units
    temp_rate = ( cur_basal_units + injection_units ) / temp_duration
    temp_rate = temp_rate * (60 / temp_duration) # convert to units per hour
    # log all data into db
    # insert meals to injection records
    self.mark_courses_for_injection(courses_to_cover, injection_id)
    return temp_rate
      

  # get the current Insulin On Board
  def get_cur_iob_units(self):
    sql_select_iob = "select datetime_iob, iob from iob where \
                    datetime_iob = (select max(datetime_iob) from iob \
                    where datetime_iob < now() and datetime_iob > now()-10min)"
    self.db.execute(sql_select_iob)
    iob = 0 
    for row in self.db.fetchall():
      iob = row["iob"]
    return iob

  def get_cur_bg_units(self):
    sql_select_sgvs = "select datetime_recorded, sgv from sgv where \
                    datetime_recorded = (select max(datetime_recorded) from sgv \
                    where datetime_recorded < now() and datetime_iob > now()-20min)"
    self.db.execute(sql_select_sgvs)
    cur_bg = None 
    for row in self.db.fetchall():
      cur_bg = row["sgv"]
    cur_bg_units = (cur_bg - self.oloop_config.get_target_bg()) / self.cloop_config.get_bg_sensitivity()
    return cur_bg_units

  def get_carbs_to_cover_units(self):
    units_for_carbs = 0
    courses_to_cover = self.get_courses_to_cover()
    carbs = 0
    for course in courses_to_cover:
      carbs += course["carbs"]
    units_for_carbs = carbs / self.cloop_config.get_carb_sensitivity()
    return units_for_carbs, courses_to_cover

  def get_courses_to_cover(self):
    sql_get_courses = "select * from courses where \
                    datetime_consumption > now()-45 and datetime_consumption < now()+50 \
                    and course_id not in (select course_id from courses_to_injections where \
                    injection_id in (select injection_id from injections where status = 'sucessful'))"
    self.db.execute(sql_get_courses)
    return self.db.fetchall()

  def mark_courses_for_injection(self, courses, injection_id):
    sql_to_mark = "insert into courses_to_injections (course_id, injection_id) values "
    for course in courses:
      sql_to_mark += "(" + course["course_id"] + ", " + injection_id + "),"
    sql_to_mark = sql_to_mark[:1]
    self.db.execute(sql_to_mark)

  def get_cur_basal_units(self):
    return 1.1

  def get_all_meal_carbs_absorbed(self):
    sql_get_last_injection = "select * from injections where status = 'successful' and \
                    injection_id in (select distinct injection_id from courses_to_injections) \
                    order by datetime_delivered desc limit 1"
    self.db.execute(sql_get_last_injection)
    last_injection = None
    for row in self.db.fetchall():
      last_injection = row
    if last_injection = None:
      return True
    if last_injection["datetime_delivered"] + 2 < now():
      return True
    else:
      return False

  def get_current_bg(self):
    sql_select_sgvs = "select datetime_recorded, sgv from sgv where \
                    datetime_recorded = (select max(datetime_recorded) from sgv \
                    where datetime_recorded < now() and datetime_iob > now()-20min)"
    self.db.execute(sql_select_sgvs)
    cur_bg = None 
    for row in self.db.fetchall():
      cur_bg = row["sgv"]
    return cur_bg

  def is_active_injection(self):
    sql_get_active_injections = "select * from injections where \
                    status = 'successful' and datetime_delivered > now()-31"
    rows = self.db.fetchall()
    if len(rows) > 0:
      return True
    else:
      return False



if __name__ == '__main__':
  process = injection_process()
  process.process_injection()
