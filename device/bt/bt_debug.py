import bluetooth
import dbus


def debug_bt_bt():
    print "looking for nearby devices..."
    nearby_devices = bluetooth.discover_devices(lookup_names=True, flush_cache=True, duration=20)
    print "found %d devices" % len(nearby_devices)
    for addr, name in nearby_devices:
        print " %s - %s" % (addr, name)
        print_services(bluetooth.find_service(address=addr))


def debug_bt_dbus():
    print "Testing via dbus"
    bus = dbus.SystemBus()
    if bus is None:
        print " dbus was none"
    else:
        print " found dbus"

    manager = dbus.Interface(bus.get_object('org.bluez', '/'), 'org.bluez.Manager')
    if manager is None:
        print " manager was none"
    else:
        print " found manager"

    adapter_path = manager.DefaultAdapter()
    if adapter_path is None:
        print " adapterPath was none"
    else:
        print " found adapterPath"

    adapter = dbus.Interface(bus.get_object('org.bluez', adapter_path), 'org.bluez.Adapter')
    if adapter_path is None:
        print " adapter was none"
    else:
        print " found adapter"

    devices = adapter.ListDevices()

    print " found " + str(len(devices)) + " devices "
    for devicePath in adapter.ListDevices():
        device = dbus.Interface(bus.get_object('org.bluez', devicePath), 'org.bluez.Device')
        device_props = device.GetProperties()
        print device_props["Address"]


def debug_find_service():
    services = bluetooth.find_service(name="BTSync")
    print_services(services)
    services = bluetooth.find_service(uuid="94f39d29-7d6d-437d-973b-fba39e49d4ee")
    print_services(services)


def print_services(services):
    if services is None:
        print "FAIL: Services is None"
    if len(services) <=0:
        print "FAIL: Services size is zero"
    for i in range(len(services)):
        print " Name: %s" % (services["name"])
        print " Description: %s" % (services["description"])
        print " Protocol: %s" % (services["protocol"])
        print " Provider: %s" % (services["provider"])
        print " Port: %s" % (services["port"])
        print " Service id: %s" % (services["service-id"])
        print ""
        print ""

if __name__ == '__main__':
    try:
        debug_bt_bt()
    except:
        print "FAILED: to debug via bluetooth interface"
    try:
        debug_bt_dbus()
    except:
        print "FAILED: to debug via dbus interface"