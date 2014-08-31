from unittest import TestCase
from device.processes.injection_process import InjectionProcess

__author__ = 'erobinson'


class TestInjectionProcess(TestCase):
    process = InjectionProcess()

    def test_meal_without_iob(self):
        # test 1 - meal without iob
        self.clear_db_tables()
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn');")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            self.fail("Fail: Test 1: too many injections (" + str(len(rows)) + ")")
        if rows[0][0] != 3.31:
            self.fail("Fail: Test 1: units_intended wrong (" + str(rows[0][0]) + ")")
        if rows[0][1] != 3.31:
            self.fail("Fail: Test 1: units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 1: status wrong (" + str(rows[0][3]) + ")")

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 45:
            self.fail("Fail: Test 1: wrong number of rows for iob (" + str(len(rows)) + ")")

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            self.fail("Fail: Test 1: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_meal_with_iob(self):
        # Test 2 - meal with iob
        self.clear_db_tables()
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.db.execute(
            "insert into iob (datetime_iob, iob, iob_bg) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10, 420)")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            self.fail("Fail: Test 2: too many injections (" + str(len(rows)) + ")")
        if rows[0][0] != 1.31:
            self.fail("Fail: Test 2: units_intended wrong (" + str(rows[0][0]) + ")")
        if rows[0][1] != 1.31:
            self.fail("Fail: Test 2: units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][2] is not None:
            self.fail("Fail: Test 2: temp_rate wrong (" + str(rows[0][2]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 2: status wrong (" + str(rows[0][3]) + ")")

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 46:
            self.fail("Fail: Test 2: wrong number of rows for iob (" + str(len(rows)) + ")")

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            self.fail("Fail: Test 2: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_negative_correction(self):
        # Test 3 - negative correction
        self.clear_db_tables()
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 10, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.db.execute(
            "insert into iob (datetime_iob, iob, iob_bg) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10, 420)")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            self.fail("Fail: Test 3: too many injections (" + str(len(rows)) + ")")
        if rows[0][0] != -2.23077:
            self.fail("Fail: Test 3: units_intended wrong (" + str(rows[0][0]) + ")")
        if rows[0][1] != -.55:
            self.fail("Fail: Test 3: units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][2] != 0.0:
            self.fail("Fail: Test 3: temp_rate wrong (" + str(rows[0][2]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 3: status wrong (" + str(rows[0][3]) + ")")

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 49:
            self.fail("Fail: Test 3: wrong number of rows for iob (" + str(len(rows)) + ")")

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            self.fail("Fail: Test 3: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_no_sgv_high_carbs(self):
        # Test 4 - no sgv / high carbs
        self.clear_db_tables()
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.db.execute(
            "insert into iob (datetime_iob, iob, iob_bg) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2, 180), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10, 420)")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            self.fail("Fail: Test 4: too many injections (" + str(len(rows)) + ")")
        if rows[0][0] != 6.92:
            self.fail("Fail: Test 4: units_intended wrong (" + str(rows[0][0]) + ")")
        if rows[0][1] != 6.92:
            self.fail("Fail: Test 4: units_delivered wrong (" + str(rows[0][1]) + ")")
        if rows[0][2] is not None:
            self.fail("Fail: Test 4: temp_rate wrong (" + str(rows[0][2]) + ")")
        if rows[0][3] != "delivered":
            self.fail("Fail: Test 4: status wrong (" + str(rows[0][3]) + ")")

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 46:
            self.fail("Fail: Test 4: wrong number of rows for iob (" + str(len(rows)) + ")")

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            self.fail("Fail: Test 4: wrong number of rows for alerts (" + str(len(rows)) + ")")

    def test_automode(self):
        # check no injections when off
        self.clear_db_tables()
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'off')")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            self.fail("Fail: Test 5: too many injections (" + str(len(rows)) + ")")

        # check no bolus when lowsOnly
        self.clear_db_tables()
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'lowsOnly')")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            self.fail("Fail: Test 5: positive injection when in lows only (" + str(len(rows)) + ")")

        # check lows when low only
        self.clear_db_tables()
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'lowsOnly')")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) != 1:
            self.fail("Fail: Test 5: no temp when in lows only (" + str(len(rows)) + ")")

        # check lows when fullOn
        self.clear_db_tables()
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) != 1:
            self.fail("Fail: Test 5: no injection when in fullOn (" + str(len(rows)) + ")")

        # check bolus when fullOn
        self.clear_db_tables()
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'fullOn')")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) != 1:
            self.fail("Fail: Test 5: no injection when in fullOn (" + str(len(rows)) + ")")

    def clear_db_tables(self):
        self.process.db.execute("delete from courses_to_injections")
        self.process.db.execute("delete from injections")
        self.process.db.execute("delete from iob")
        self.process.db.execute("delete from courses")
        self.process.db.execute("delete from sgvs")
        self.process.db.execute("delete from automode_switch")
        self.process.db.execute("delete from alerts")