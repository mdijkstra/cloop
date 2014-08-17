from unittest import TestCase

__author__ = 'erobinson'
from device.processes.sync_device_phone import DeviceDBTransData

class TestDeviceDBTransData(TestCase):
    transData = DeviceDBTransData()

    def test_course_xml_to_sql_insert(self):
        xml_test = "<courses><course>" \
                   "<serv_quantity>0.0</serv_quantity>" \
                   "<food_id>0</food_id>" \
                   "<course_id>3</course_id>" \
                   "<comment></comment>" \
                   "<carbs>3</carbs>" \
                   "<datetime_ideal_injection>2014-08-17T15:32:25</datetime_ideal_injection>" \
                   "<datetime_consumption>2014-08-17T15:32:25</datetime_consumption>" \
                   "</course></courses>"
        self.transData.import_sgvs(xml_test)

