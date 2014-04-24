# file: rfcomm-server.py
# auth: Albert Huang <albert@csail.mit.edu>
# desc: simple demonstration of a server application that uses RFCOMM sockets
#
# $Id: rfcomm-server.py 518 2007-08-10 07:20:07Z albert $

from bluetooth import *

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
latest_sg_file = "/home/pi/diabetes/main/decoding-carelink/logs/analyze/latest-sg.xml"
latest_sg_xml = ""
with open(latest_sg_file, 'r') as file:
	latest_sg_xml = file.read()
client_sock.send(latest_sg_xml + "<EOM>")
print("disconnecting..")

client_sock.close()
server_sock.close()
print("all done with the connection")

def get_value_from_xml (string, tag):
    start = string.index("<"+tag+">")
    end = string.index("</"+tag+">", start)
    return string[start+len(tag)+2:end]

print (data)
portion = get_value_from_xml(data, "portion")
print (portion)
carbs = get_value_from_xml(portion, "carbs")
time_to_deliver = carbs * 10

import os
from time import sleep
serial = "584923"
port = "/dev/ttyUSB0"
directory = "/home/erobinson/diabetes/sus-res-test/decoding-carelink/bin"
command = "sudo python "+directory+"mm-set-suspend.py --serial "+serial+" --port "+port+" --verbose resume"
print(command)
#os.system(command)
sleep(time_to_deliver)
command = "sudo python "+directory+"mm-set-suspend.py --serial "+serial+" --port "+port+" --verbose suspend"
print(command)
#os.system(command)

print "all done"
