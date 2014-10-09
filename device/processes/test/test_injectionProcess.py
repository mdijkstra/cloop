from unittest import TestCase
from device.processes.injection_process import InjectionProcess
from device.processes.injection_process import Injection
from device.processes import cloop_db
import datetime
import time

__author__ = 'erobinson'


class TestInjectionProcess(TestCase):
    process = InjectionProcess()
    # cloop_db = cloop_db.CloopDB()

    def test_meal_without_iob(self):
        # test 1 - meal without iob
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn');")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        self.assertEqual(len(rows), 1, "Fail: Test 1: too many injections (" + str(len(rows)) + ")")
        self.assertEqual(rows[0][0], 3.31, "units_intended wrong (" + str(rows[0][0]) + ")")
        self.assertEqual(rows[0][1], None, " units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 1: status wrong (" + str(rows[0][3]) + ")")

        rows = self.process.cloop_db.select("select iob, datetime_iob from iob")
        self.assertEqual(len(rows), 0, "Fail: Test 1: wrong number of rows for iob (" + str(len(rows)) + ")")

        rows = self.process.cloop_db.select("select * from alerts")
        if len(rows) != 2:
            self.fail("Fail: Test 1: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_meal_with_iob(self):
        # Test 2 - meal with iob
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.cloop_db.execute(
            "insert into iob (datetime_iob, iob, iob_bg) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10, 420)")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        self.assertEqual(len(rows), 1, " Should be 1 inj: " + str(len(rows)))
        if rows[0][0] != 1.31:
            self.fail("Fail: Test 2: units_intended wrong (" + str(rows[0][0]) + ")")
        self.assertEqual(rows[0][1], None, "units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][2] is not None:
            self.fail("Fail: Test 2: temp_rate wrong (" + str(rows[0][2]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 2: status wrong (" + str(rows[0][3]) + ")")

        rows = self.process.cloop_db.select("select iob, datetime_iob from iob")
        self.assertEqual(len(rows), 3, "Fail: Test 2: wrong number of rows for iob (" + str(len(rows)) + ")")

        rows = self.process.cloop_db.select("select * from alerts")
        if len(rows) != 2:
            self.fail("Fail: Test 2: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_negative_correction(self):
        # Test 3 - negative correction
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 10, now() + interval 5 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.cloop_db.execute(
            "insert into iob (datetime_iob, iob, iob_bg) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10, 420)")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) > 1:
            self.fail("Fail: Test 3: too many injections (" + str(len(rows)) + ")")
        if rows[0][0] != -2.23077:
            self.fail("Fail: Test 3: units_intended wrong (" + str(rows[0][0]) + ")")
        self.assertEqual(rows[0][1], None, "units_delivered wrong (" + str(rows[0][1]) + ")")
        self.assertEqual(rows[0][2], 0.0, "temp_rate wrong (" + str(rows[0][2]) + ")")
        self.assertEqual(rows[0][3], "delivered", "status wrong (" + str(rows[0][3]) + ")")

        rows = self.process.cloop_db.select("select iob, datetime_iob from iob")
        self.assertEqual(len(rows), 3, "Fail: Test 3: wrong number of rows for iob (" + str(len(rows)) + ")")

        rows = self.process.cloop_db.select("select * from alerts")
        if len(rows) != 2:
            self.fail("Fail: Test 3: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_no_sgv_high_carbs(self):
        # Test 4 - no sgv / high carbs
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.cloop_db.execute(
            "insert into iob (datetime_iob, iob, iob_bg) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10, 420)")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) > 1:
            self.fail("Fail: Test 4: too many injections (" + str(len(rows)) + ")")
        if rows[0][0] != 6.92:
            self.fail("Fail: Test 4: units_intended wrong (" + str(rows[0][0]) + ")")
        self.assertEqual(rows[0][1], None, "units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][2] is not None:
            self.fail("Fail: Test 4: temp_rate wrong (" + str(rows[0][2]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 4: status wrong (" + str(rows[0][3]) + ")")

        rows = self.process.cloop_db.select("select iob, datetime_iob from iob")
        self.assertEqual(len(rows), 3, "Fail: Test 4: wrong number of rows for iob (" + str(len(rows)) + ")")

        rows = self.process.cloop_db.select("select * from alerts")
        if len(rows) != 2:
            self.fail("Fail: Test 4: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_automode(self):
        # check no injections when off
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'off')")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) > 1:
            self.fail("Fail: Test 5: too many injections (" + str(len(rows)) + ")")

        # check no bolus when lowsOnly
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'lowsOnly')")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) > 1:
            self.fail("Fail: Test 5: positive injection when in lows only (" + str(len(rows)) + ")")

        # check lows when low only
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'lowsOnly')")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) != 1:
            self.fail("Fail: Test 5: no temp when in lows only (" + str(len(rows)) + ")")

        # check lows when fullOn
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) != 1:
            self.fail("Fail: Test 5: no injection when in fullOn (" + str(len(rows)) + ")")

        # check bolus when fullOn
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.cloop_db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.process_injection()
        rows = self.process.cloop_db.select("select units_intended, units_delivered, temp_rate, status from injections")
        if len(rows) != 1:
            self.fail("Fail: Test 5: no injection when in fullOn (" + str(len(rows)) + ")")

    def test_is_recent_injection(self):
        self.process.cloop_db.clear_db()
        result = self.process.is_recent_injection("bolus")
        self.assertEqual(result, False, "No injections: " + str(result))
        injection = Injection()
        injection.injection_type = "bolus"
        injection.cur_iob_units = 1.0
        injection.cur_bg_units = 2.5
        injection.cur_bg = 195
        injection.carbs_to_cover = 15
        injection.carbs_units = 1.0
        injection.cur_basal_units = 1.1
        injection.all_meal_carbs_absorbed = True
        injection.correction_units = 1.5
        injection.injection_units = 2.5
        injection.temp_rate = None
        self.process.create_injection_rec(injection)
        result = self.process.is_recent_injection("bolus")
        self.assertEqual(result, False, "No injections: " + str(result))

    def test_get_all_meal_carbs_absorbed(self):
        self.process.cloop_db.clear_db()
        result = self.process.get_all_meal_carbs_absorbed()
        self.assertEqual(result, True, "No courses to absorb: " + str(result))
        injection = Injection()
        injection.injection_type = "bolus"
        injection.cur_iob_units = 1.0
        injection.cur_bg_units = 2.5
        injection.cur_bg = 195
        injection.carbs_to_cover = 15
        injection.carbs_units = 1.0
        injection.cur_basal_units = 1.1
        injection.all_meal_carbs_absorbed = True
        injection.correction_units = 1.5
        injection.injection_units = 2.5
        injection.temp_rate = None
        self.process.create_injection_rec(injection)
        result = self.process.get_all_meal_carbs_absorbed()
        self.assertEqual(result, True, "No courses to absorb: " + str(result))

    def test_create_injection_rec(self):
        self.process.cloop_db.clear_db()
        injection = Injection()
        injection.injection_type = "bolus"
        injection.cur_iob_units = 1.0
        injection.cur_bg_units = 2.5
        injection.cur_bg = 195
        injection.carbs_to_cover = 15
        injection.carbs_units = 1.0
        injection.cur_basal_units = 1.1
        injection.all_meal_carbs_absorbed = True
        injection.correction_units = 1.5
        injection.injection_units = 2.5
        injection.temp_rate = None
        self.process.create_injection_rec(injection)

    def test_is_recent_recommendation(self):
        self.process.cloop_db.clear_db()
        self.process.add_alert(datetime.datetime.now(), 'injection_process.should_bolus', 'info', 'Test Action',
                               'Test Action')
        self.process.add_alert(datetime.datetime.now(), 'injection_process.should_temp', 'info', 'Test Action',
                               'Test Action')
        self.assertEqual(self.process.is_recent_recommendation("bolus"), True, "Should be recent bolus.")
        self.assertEqual(self.process.is_recent_recommendation("square"), True, "Should be recent square.")


    def test_get_current_bg(self):
        self.process.cloop_db.clear_db()
        self.process.cloop_db.execute("insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now(), 90)")
        time.sleep(1)
        cur_bg = self.process.get_current_bg()
        self.assertEqual(cur_bg, 90, "Should be 90 : " + str(cur_bg))
