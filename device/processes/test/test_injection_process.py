from unittest import TestCase
from device.processes.injection_process import InjectionProcess

__author__ = 'erobinson'


class TestInjectionProcess(TestCase):
    process = InjectionProcess()

    def test_meal_without_iob(self):
        failed = False
        # test 1 - meal without iob
        self.process.db.execute("delete from courses_to_injections")
        self.process.db.execute("delete from injections")
        self.process.db.execute("delete from iob")
        self.process.db.execute("delete from courses")
        self.process.db.execute("delete from sgvs")
        self.process.db.execute("delete from automode_switch")
        self.process.db.execute("delete from alerts")
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes');")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            failed = True
            print "Fail: Test 1: too many injections (" + str(len(rows)) + ")"
        if rows[0][0] != 4.31:
            failed = True
            print "Fail: Test 1: units_intended wrong (" + str(rows[0][0]) + ")"
        if rows[0][1] != 4.31:
            failed = True
            print "Fail: Test 1: units_delivered wrong (" + str(rows[0][1]) + ")"
        if rows[0][3] != "successful":
            failed = True
            print "Fail: Test 1: status wrong (" + str(rows[0][3]) + ")"

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 45:
            failed = True
            print "Fail: Test 1: wrong number of rows for iob (" + str(len(rows)) + ")"

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            failed = True
            print "Fail: Test 1: wrong number of rows for alerts (" + str(len(rows)) + ")"

        if failed:
            print "Fail: Test 1 (meal without iob) failed thus stopping"
            self.fail()
        else:
            print "Test 1 (meal without iob) passes!"

    def test_meal_with_iob(self):
        failed = False
        # Test 2 - meal with iob
        self.process.db.execute("delete from courses_to_injections")
        self.process.db.execute("delete from injections")
        self.process.db.execute("delete from iob")
        self.process.db.execute("delete from courses")
        self.process.db.execute("delete from sgvs")
        self.process.db.execute("delete from automode_switch")
        self.process.db.execute("delete from alerts")
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 150)")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 30, now() + interval 30 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes')")
        self.process.db.execute(
            "insert into iob (datetime_iob, iob) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10)")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            failed = True
            print "Fail: Test 2: too many injections (" + str(len(rows)) + ")"
        if rows[0][0] != 2.31:
            failed = True
            print "Fail: Test 2: units_intended wrong (" + str(rows[0][0]) + ")"
        if rows[0][1] != 2.31:
            failed = True
            print "Fail: Test 2: units_delivered wrong (" + str(rows[0][1]) + ")"
        if rows[0][2] is not None:
            failed = True
            print "Fail: Test 2: temp_rate wrong (" + str(rows[0][2]) + ")"
        if rows[0][3] != "successful":
            failed = True
            print "Fail: Test 2: status wrong (" + str(rows[0][3]) + ")"

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 46:
            failed = True
            print "Fail: Test 2: wrong number of rows for iob (" + str(len(rows)) + ")"

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            failed = True
            print "Fail: Test 2: wrong number of rows for alerts (" + str(len(rows)) + ")"

        if failed:
            print "Fail: Test 2 (meal with iob) failed thus stopping"
            self.fail()
        else:
            print "Test 2 (meal with iob) passes!"

    def test_negative_correction(self):
        failed = False
        # Test 3 - negative correction
        self.process.db.execute("delete from courses_to_injections")
        self.process.db.execute("delete from injections")
        self.process.db.execute("delete from iob")
        self.process.db.execute("delete from courses")
        self.process.db.execute("delete from sgvs")
        self.process.db.execute("delete from automode_switch")
        self.process.db.execute("delete from alerts")
        self.process.db.execute(
            "insert into sgvs (device_id, datetime_recorded, sgv) values (123456, now() - interval 5 minute, 90)")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 10, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes')")
        self.process.db.execute(
            "insert into iob (datetime_iob, iob) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10)")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            failed = True
            print "Fail: Test 3: too many injections (" + str(len(rows)) + ")"
        if rows[0][0] != -3.23077:
            failed = True
            print "Fail: Test 3: units_intended wrong (" + str(rows[0][0]) + ")"
        if rows[0][1] != -.55:
            failed = True
            print "Fail: Test 3: units_delivered wrong (" + str(rows[0][1]) + ")"
        if rows[0][2] != 0.0:
            failed = True
            print "Fail: Test 3: temp_rate wrong (" + str(rows[0][2]) + ")"
        if rows[0][3] != "successful":
            failed = True
            print "Fail: Test 3: status wrong (" + str(rows[0][3]) + ")"

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 49:
            failed = True
            print "Fail: Test 3: wrong number of rows for iob (" + str(len(rows)) + ")"

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            failed = True
            print "Fail: Test 3: wrong number of rows for alerts (" + str(len(rows)) + ")"

        if failed:
            print "Fail: Test 3 (negative correction) failed thus stopping"
            self.fail()
        else:
            print "Test 3 (negative correction) passes!"

    def test_no_sgv_high_carbs(self):
        failed = False
        # Test 4 - no sgv / high carbs
        self.process.db.execute("delete from courses_to_injections")
        self.process.db.execute("delete from injections")
        self.process.db.execute("delete from iob")
        self.process.db.execute("delete from courses")
        self.process.db.execute("delete from sgvs")
        self.process.db.execute("delete from automode_switch")
        self.process.db.execute("delete from alerts")
        self.process.db.execute(
            "insert into courses (course_id, carbs, datetime_consumption) values (1, 90, now() + interval 5 minute)")
        self.process.db.execute(
            "insert into automode_switch (automode_switch_id, datetime_recorded, is_on) values (1, now(), 'yes')")
        self.process.db.execute(
            "insert into iob (datetime_iob, iob) values \
            (from_unixtime(round(UNIX_TIMESTAMP(now() - interval 5 minute)/300)*300), 2), \
            (from_unixtime(round(UNIX_TIMESTAMP(now())/300)*300), 2), \
            (from_unixtime(round(UNIX_TIMESTAMP(now() + interval 5 minute)/300)*300), 10)")
        self.process.db_conn.commit()
        self.process.process_injection()
        self.process.db.execute("select units_intended, units_delivered, temp_rate, status from injections")
        rows = self.process.db.fetchall()
        if len(rows) > 1:
            failed = True
            print "Fail: Test 4: too many injections (" + str(len(rows)) + ")"
        if rows[0][0] != 6.92:
            failed = True
            print "Fail: Test 4: units_intended wrong (" + str(rows[0][0]) + ")"
        if rows[0][1] != 6.92:
            failed = True
            print "Fail: Test 4: units_delivered wrong (" + str(rows[0][1]) + ")"
        if rows[0][2] is not None:
            failed = True
            print "Fail: Test 4: temp_rate wrong (" + str(rows[0][2]) + ")"
        if rows[0][3] != "successful":
            failed = True
            print "Fail: Test 4: status wrong (" + str(rows[0][3]) + ")"

        self.process.db.execute("select iob, datetime_iob from iob")
        rows = self.process.db.fetchall()
        if len(rows) != 46:
            failed = True
            print "Fail: Test 4: wrong number of rows for iob (" + str(len(rows)) + ")"

        self.process.db.execute("select * from alerts")
        rows = self.process.db.fetchall()
        if len(rows) != 2:
            failed = True
            print "Fail: Test 4: wrong number of rows for alerts (" + str(len(rows)) + ")"

        if failed:
            print "Fail: Test 4 (no sgv / high carbs) failed thus stopping"
            self.fail()
        else:
            print "Test 4 (no sgv / high carbs) passes!"