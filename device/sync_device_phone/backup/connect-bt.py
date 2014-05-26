#!/usr/bin/python 

import dbus 

bus = dbus.SystemBus() 
my_mac = '30:19:66:80:2F:B2'

# service activation 
bmgr = dbus.Interface(bus.get_object('org.bluez','/org/bluez'),'org.bluez.Manager') 
bus_id = bmgr.ActivateService('input') 
imgr = dbus.Interface(bus.get_object(bus_id,'/org/bluez/input'),'org.bluez.input.Manager') 

# device creation 
path = imgr.CreateDevice(my_mac) 
idev = dbus.Interface(bus.get_object(bus_id,path),'org.bluez.input.Device') 

# host initiated connection 
idev.Connect() 
