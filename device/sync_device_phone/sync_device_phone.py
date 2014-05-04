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

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

def transmit_xml (xml_to_send):
  socket = trans_make_con( )
  trans_write(socket, xml_to_send)
  read_str = trans_read(socket)
  trans_close(socket)
  return read_str

def trans_read(socket):
  try:
    while True:
      data = socket.recv(1024)
      if len(data) == 0: break
      print("received [%s]" % data)
      if "</EOM>" in data: break
  except IOError:
    pass

def trans_write(socket):
  socket.send(latest_sg_xml + "<EOM>")

def trans_make_con ( ):
  service = bluetooth.find_service(address = phone_mac, uuid = uuid)
  if len(service) == 0:
    print "Couldn't find phone BT Service"
    return None


def trans_close(socket):
  socket.close()


def get_value_from_xml (string, tag):
    start = string.index("<"+tag+">")
    end = string.index("</"+tag+">", start)
    return string[start+len(tag)+2:end]

 
import bluetooth
print "looking for nearby devices..."
nearby_devices = bluetooth.discover_devices(lookup_names = True, flush_cache = True, duration = 20)
print "found %d devices" % len(nearby_devices)
for addr, name in nearby_devices:
	print " %s - %s" % (addr, name)
	for services in bluetooth.find_service(address = addr):
		print " Name: %s" % (services["name"])
		print " Description: %s" % (services["description"])
		print " Protocol: %s" % (services["protocol"])
		print " Provider: %s" % (services["provider"])
		print " Port: %s" % (services["port"])
		print " Service id: %s" % (services["service-id"])
		print ""
		print ""
import sys
import bluetooth

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
service_matches = bluetooth.find_service( uuid = uuid )
service_mathces = bluetooth.find_service( name="BTSync" )

if len(service_matches) == 0:
    print "couldn't find the service"
    sys.exit(0)

first_match = service_matches[0]
port = first_match["port"]
name = first_match["name"]
host = first_match["host"]

print "connecting to \"%s\" on %s" % (name, host)

sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
sock.connect((host, port))
#sock.send("hello")
try:
    while True:
        data = sock.recv(1024)
        if len(data) == 0: break
        print("received [%s]" % data)
        if "</EOM>" in data: break
except IOError:
    pass

print "Done reading now going to write"
latest_sg_file = "/home/pi/diabetes/main/decoding-carelink/logs/analyze/latest-sg.xml"
latest_sg_xml = ""
with open(latest_sg_file, 'r') as file:
        latest_sg_xml = file.read()
sock.send(latest_sg_xml + "<EOM>")

sock.close()
