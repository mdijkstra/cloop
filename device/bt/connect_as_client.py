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
