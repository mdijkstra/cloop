import dbus
import bluetooth

bus = dbus.SystemBus()

manager = dbus.Interface(bus.get_object('org.bluez', '/'), 'org.bluez.Manager')

adapterPath = manager.DefaultAdapter()

adapter = dbus.Interface(bus.get_object('org.bluez', adapterPath), 'org.bluez.Adapter')

for devicePath in adapter.ListDevices():
    device = dbus.Interface(bus.get_object('org.bluez', devicePath),'org.bluez.Device')
    deviceProperties = device.GetProperties()
    print deviceProperties["Address"]

services = bluetooth.find_service(name="BTSync")
services = bluetooth.find_service(uuid="94f39d29-7d6d-437d-973b-fba39e49d4ee")
for i in range(len(services)):
   match=services[i]
   if(match["name"]=="BTSync"):
      port=match["port"]
      name=match["name"]
      host=match["host"]

      print name, port, host

      client_socket=BluetoothSocket( RFCOMM )

      client_socket.connect((host, port))

      client_socket.send("Hello world")

      client_socket.close()

      break
