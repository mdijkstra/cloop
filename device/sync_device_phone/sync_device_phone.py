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
phone_mac = "30:19:66:80:2F:B2"

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
  socket = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
  socket.connect((service[0]["host"], service[0]["port"]))
  print "connecting to \"%s\" on %s" % (service[0]["name"], service[0]["host"])
  return socket

def trans_close(socket):
  socket.close()


def get_value_from_xml (string, tag):
    start = string.index("<"+tag+">")
    end = string.index("</"+tag+">", start)
    return string[start+len(tag)+2:end]

 
  

