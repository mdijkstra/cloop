from unittest import TestCase

__author__ = 'erobinson'
from device.processes.sync_device_phone import DeviceDBTransData


class TestDeviceDBTransData(TestCase):
    transData = DeviceDBTransData()

    def test_course_xml_to_sql_insert(self):
        self.transData.db.execute("delete from courses_to_injections")
        self.transData.db.execute("delete from courses")
        self.transData.db_conn.commit()
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

    def test_import_halts(self):
        self.transData.db.execute("truncate table halts")
        self.transData.db_conn.commit()
        xml_test = "<halts><halt>" \
                   "<halt_id>1</halt_id>" \
                   "<datetime_issued>2014-08-17T15:32:25</datetime_issued>" \
                   "</halt></halts>"
        self.transData.import_halts(xml_test)

    def test_import_automodes(self):
        self.transData.db.execute("truncate table automode_switch")
        self.transData.db_conn.commit()
        xml_test = "<automodes><automode>" \
                   "<automode_switch_id>1</automode_switch_id>" \
                   "<datetime_recorded>2014-08-17T15:32:25</datetime_recorded>" \
                   "<is_on>yes</is_on>" \
                   "</automode></automodes>"
        self.transData.import_automodes(xml_test)


