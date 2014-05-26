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
import sys
import datetime
import logging
#from bluetooth import *
#from time import sleep

dateFormat="%Y-%m-%dT%H:%M:%S"
mySQLDateFormat="%Y-%m-%d %H:%M:%S"
skip_commands = False # debug tool to skip cli commands
logging.basicConfig(level=logging.DEBUG)


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
  # TODO: possibly switch from xml to json objects
  def import_sgvs(self, sgvs_xml):
    if sgvs_xml.startswith("<sgvs>"):
      sgvs_xml = get_value_from_xml(sgvs_xml, "sgvs")
    if sgvs_xml == "":
      print "No SGVs to import. Done."
      return
    sgvs_array = get_values_from_xml(sgvs_xml, "sgv_record")
    for sgv in sgvs_array:
      self.import_sgv(sgv)

  def import_sgv(self, sgv_xml):
    device_id = get_value_from_xml(sgv_xml, "device_id")
    datetime_recorded = get_value_from_xml(sgv_xml, "datetime_recorded")
    sgv = get_value_from_xml(sgv_xml, "sgv")
    if not self.sgv_exists(datetime_recorded, device_id, sgv):
      sql = "insert into sgvs (device_id, datetime_recorded, sgv, transferred) select * from (select "
      sql += device_id + ", "
      sql += "'" + datetime_recorded + "',"
      sql += sgv + ", 'no')"
      sql += " as tmp where not exists (select sgv from sgvs where "
      sql += " device_id = " + device_id
      sql += " and datetime_recorded = '" + datetime_recorded + "'"
      sql += ") limit 1"
      try:
        print "INFO: Running sql '" + sql +"'"
        self.db.execute(sql) 
        self.db_conn.commit()
      except:
        self.db_conn.rollback()
        print "******* rolled back insert : "+sql
        
  def sgv_exists(self, datetime_recorded, device_id, sgv):
    print "TODO: implement sgv_exists()"
    return False
    
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
            % (int(course_id), int(food_id), float(serv_quantity), int(carbs), \
            datetime_consumption, datetime_ideal_injection)

    print " SQL : "+sql
    return sql


class DownloadPumpData():
  decoding_dir = "/home/pi/diabetes/decoding-carelink"
  output_file_default = "/tmp/"
  cgm_download_dir = "/tmp/"
  device_id=584923
  port="/dev/ttyUSB0"
  cur_page=18
  

  def get_latest_sgv(self):
    page_data_file = self.download_cgm_data()
    if page_data_file != 'ERRORCouldNotDownload':      
      bytes = self.file_to_bytes(page_data_file)
      last_sgv = self.get_last_sgv_from_bytes(bytes)
      return self.sgv_to_xml(last_sgv)
    else:
      return

  def download_cgm_data(self, page_num=None, include_init=True):
    # if didn't pass a page get the current
    if page_num is None:
      self.get_cur_cgm_page(include_init=True)
      include_init=False
    
    # clean out previous file
    data_file = self.cgm_download_dir+"/ReadGlucoseHistory-page-" + str(self.cur_page) + ".data"
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
    self.cli(command)

    return data_file

  def cgm_data_file_to_sgv_xml(self, file_name):
    sys.path.insert(0, self.decoding_dir)
    import list_cgm
    from list_cgm import PagedData
    records = []
    with open(file_name, 'rb') as stream:
#      for stream in file_name:
      page = PagedData(stream)
      records.extend(page.decode())
    
    # convert the records to sgv xml
    xml = "<sgvs>"
    for record in records:
      if record['name'] == 'GlucoseSensorData':
        xml += self.sgv_to_xml(record['sgv'],record['date'])
    xml += "</sgvs>"
    return xml

  def run_sticky(self):
    command = "sudo python"
    command += " " + self.decoding_dir + "/decocare/sticky.py"
    command += " " + self.port
    self.cli(command)
   
  def get_cur_cgm_page(self, include_init=True):
    # clear the previous download file 
    download_file = self.cgm_download_dir+"/ReadCurGlucosePageNumber.data"
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
    self.cli(command)

    # decode the page number
    if not os.path.isfile(download_file):
       print "ERROR: Current Page was not downloaded"
       return 'ERRORNotDownloaded'
    cur_page_data = self.file_to_bytes(download_file)
    cur_page = int(cur_page_data[5]) - 1 #array style count
    if cur_page < 0 or cur_page > 500:
      print "ERROR: Could not download the current page"
      return 'ERRORCouldNotParse'
    print "INFO: Found the current page to be '" + str(cur_page) + "'"
    self.cur_page = cur_page
    return self.cur_page

  def cli(self, command):
    if not skip_commands:
      print "INFO: About to execute command: \n\t " + command
      os.system(command)

  def file_to_bytes(self, file_name):
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
    for i in range(0, len(bytes)-10):
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
    xml =  "<sgv_record>"
    xml += "<device_id>" + str(self.device_id) + "</device_id>"
#    xml += "<datetime_recorded>" + datetime.datetime.now().strftime(mySQLDateFormat) + "</datetime_recorded>"
    xml += "<datetime_recorded>" + date_isoformat + "</datetime_recorded>"
    xml += "<sgv>" + str(sgv) + "</sgv>"
    xml += "</sgv_record>"
    return xml
    

if __name__ == '__main__':
  # downlaod the data from the pump
  # parse it to get the latest sgv
  download_pump = DownloadPumpData()
  file_output = download_pump.download_cgm_data()
  cgm_xml = download_pump.cgm_data_file_to_sgv_xml(file_output)
#  last_sgv_xml = download_pump.get_latest_sgv()
  # import that sgv into the db
  db_trans = PumpDeviceDBTrans()
#  db_trans.import_sgv(latest_sgv_xml)
  db_trans.import_sgvs(cgm_xml)
  




  
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

