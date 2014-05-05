# file: rfcomm-server.py
# auth: Albert Huang <albert@csail.mit.edu>
# desc: simple demonstration of a server application that uses RFCOMM sockets
#
# $Id: rfcomm-server.py 518 2007-08-10 07:20:07Z albert $
from bluetooth import *
from array import *
import MySQLdb

db_conn = MySQLdb.connect(host="localhost", # your host, usually localhost
                             port=33062,
                     user="cloop", # your username
                      passwd="cloop", # your password
                      db="cloop") # name of the data base

# you must create a Cursor object. It will let
#  you execute all the queries you need
db = db_conn.cursor() 

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "ERSampleServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ]
#                   ,protocols = [ OBEX_UUID ] 
                    )

#advertise_service(server_sock, "ER Sample Server", uuid)
                   
print("Waiting for connection on RFCOMM channel %d" % port)

client_sock, client_info = server_sock.accept()
print("Accepted connection from ", client_info)

try:
    while True:
        data = client_sock.recv(1024)
        if len(data) == 0: break
        print("received [%s]" % data)
        if "</EOM>" in data: break
except IOError:
    pass

print "Done reading now going to write"
client_sock.send("transfer successful") # not really true b/c the data is parsed below but okay for now
print("disconnecting..")

client_sock.close()
server_sock.close()
print("all done with the connection")

def get_value_from_xml (xml, tag):
    start = xml.index("<"+tag+">")
    end = xml.index("</"+tag+">", start)
    return xml[start+len(tag)+2:end]

def get_values_from_xml (full_xml, tag):
    a = []
    i = 0
    while full_xml != "":
        a.append(get_value_from_xml(full_xml, tag))
        end_xml_tag = "</"+tag+">"
        end_tag_loc = full_xml.index(end_xml_tag)
        full_xml = full_xml[end_tag_loc+len(end_xml_tag):]
    return a

def course_xml_to_sql_insert(xml):
    course_id = get_value_from_xml(xml, "course_id")
    food_id = get_value_from_xml(xml, "food_id")
    serv_quantity = get_value_from_xml(xml, "serv_quantity")
    carbs = get_value_from_xml(xml, "carbs")
    datetime_consumption = get_value_from_xml(xml, "datetime_consumption")
    datetime_ideal_injection = get_value_from_xml(xml, "datetime_ideal_injection")
    sql = " insert into courses (course_id, food_id, serv_quantity, carbs, \
            datetime_consumption, datetime_ideal_injection) \
            values ( %d, %d, %f, %d, '%s','%s')" \
            % (int(course_id), int(food_id), float(serv_quantity), int(carbs), \
            datetime_consumption, datetime_ideal_injection)
        
    # print " SQL : "+sql
    return sql

def add_courses_to_db (courses_xml):
    courses = get_values_from_xml(courses_xml, "course")
    for c_xml in courses:
        insert_sql = course_xml_to_sql_insert(c_xml)
        try:
            print "about to run : "+insert_sql
            db.execute(insert_sql)    
            db_conn.commit()
        except:
            db_conn.rollback()
            print "******* rolled back insert "

            
courses_transfered = get_value_from_xml(data, "courses")
add_courses_to_db(courses_transfered)


#print (data)
#portion = get_value_from_xml(data, "portion")
#print (portion)
#carbs = get_value_from_xml(portion, "carbs")
#time_to_deliver = carbs * 10

#import os
#from time import sleep
#serial = "584923"
#port = "/dev/ttyUSB0"
#directory = "/home/erobinson/diabetes/sus-res-test/decoding-carelink/bin"
#command = "sudo python "+directory+"mm-set-suspend.py --serial "+serial+" --port "+port+" --verbose resume"
#print(command)
#os.system(command)
#sleep(time_to_deliver)
#command = "sudo python "+directory+"mm-set-suspend.py --serial "+serial+" --port "+port+" --verbose suspend"
#print(command)
#os.system(command)

print "all done"
