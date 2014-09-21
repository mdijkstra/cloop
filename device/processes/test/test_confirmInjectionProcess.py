from unittest import TestCase
from device.processes import cloop_db
from device.processes import confirm_injection_process
from device.processes import pump_interface

__author__ = 'erobinson'


class TestConfirmInjectionProcess(TestCase):
    cloop_db = cloop_db.CloopDB()
    confirm = confirm_injection_process.ConfirmInjectionProcess()
    pump_interface = pump_interface.PumpInterface()

    record1_str = '[{\
                "programmed": 1.0,\
                "_type": "Bolus",\
                "_description": "Bolus 2014-08-31T20:14:33 head[4], body[0] op[0x01]",\
                "duration": 0,\
                "timestamp": "2014-08-31T20:14:33",\
                "type": "normal",\
                "amount": 1.0 \
            }, \
            { \
                "programmed": 2.0,\
                "_type": "Bolus",\
                "_description": "Bolus 2014-08-31T20:14:33 head[4], body[0] op[0x01]",\
                "duration": 0,\
                "timestamp": "2014-08-31T20:02:33",\
                "type": "normal",\
                "amount": 1.5 \
            }, \
            { \
                "timestamp": "2014-09-03T17:24:08", \
                "_type": "TempBasal", \
                "rate": 0.0, \
                "_description": "TempBasal 2014-09-03T17:24:08 head[2], body[1] op[0x33]" \
            }, \
            { \
                "timestamp": "2014-09-03T17:24:08", \
                "_type": "TempBasalDuration", \
                "duration (min)": 30, \
                "_description": "TempBasalDuration 2014-09-03T17:24:08 head[2], body[0] op[0x16]" \
            }, \
            { \
                "timestamp": "2014-09-03T17:31:06", \
                "_type": "TempBasal", \
                "rate": 0.0, \
                "_description": "TempBasal 2014-09-03T17:31:06 head[2], body[1] op[0x33]" \
            }, \
            { \
                "timestamp": "2014-09-03T17:31:06", \
                "_type": "TempBasalDuration", \
                "duration (min)": 30, \
                "_description": "TempBasalDuration 2014-09-03T17:31:06 head[2], body[0] op[0x16]" \
            }, { \
                "_type": "BolusWizard", \
                "bg": 250, \
                "correction_estimate": 0.8, \
                "unknown_byte[10]": 0, \
                "_description": "BolusWizard 2014-09-20T21:38:41 head[2], body[13] op[0x5b]", \
                "timestamp": "2014-09-20T21:38:41", \
                "sensitivity": 30, \
                "carb_input": 0, \
                "bg_target_high": 130, \
                "unabsorbed_insulin_total": 2.9, \
                "_byte[5]": 40, \
                "unabsorbed_insulin_count": "??", \
                "_byte[7]": 0, \
                "unknown_byte[8]": 0, \
                "carb_ratio": 14, \
                "food_estimate": 0.0, \
                "bg_target_low": 100, \
                "bolus_estimate": 1.1 \
            } \
            ]'

    def __init__(self, param1):
        super(TestConfirmInjectionProcess, self).__init__(param1)
        self.records = self.pump_interface.parse_json(self.record1_str)

    def test_save_or_update_injection(self):
        self.cloop_db.clear_db()
        self.cloop_db.execute(
            "insert into injections (datetime_intended, units_intended, injection_type, status) values "
            "('2014-08-31T20:00:00', 2.0, 'bolus', 'delivered')")
        self.confirm.save_or_update_injection(self.records[0], None)
        self.confirm.save_or_update_injection(self.records[1], None)
        self.check_injections()

        self.confirm.save_or_update_injection(self.records[0], None)
        self.confirm.save_or_update_injection(self.records[1], None)
        self.check_injections()

        self.confirm.save_or_update_injection(self.records[2], self.records[3])
        self.confirm.save_or_update_injection(self.records[4], self.records[5])

    def check_injections(self):
        sql_check_injections = "select injection_id, units_delivered, datetime_delivered, status " \
                               "from injections order by datetime_intended desc"
        injections = self.cloop_db.select(sql_check_injections)
        self.assertEqual(len(injections), 2, "No injections " + str(len(injections)))

        self.assertEqual(injections[0][1], 1, "Wrong units delivered " + str(injections[0][1]))
        self.assertEqual(str(injections[0][2]), '2014-08-31 20:14:33',
                         "Wrong datetime delivered " + str(injections[0][2]))
        self.assertEqual(injections[0][3], 'confirmed', "Wrong status " + str(injections[0][3]))

        self.assertEqual(injections[1][1], 1.5, "Wrong units delivered " + str(injections[1][1]))
        self.assertEqual(str(injections[1][2]), '2014-08-31 20:02:33',
                         "Wrong datetime delivered " + str(injections[1][2]))
        self.assertEqual(injections[1][3], 'confirmed', "Wrong status " + str(injections[1][3]))

    def test_update_iob(self):
        self.cloop_db.clear_db()
        self.confirm.save_or_update_injection(self.records[0], None)
        self.confirm.update_iob()
        count = self.cloop_db.select("select count(*) from iob")
        self.assertEqual(count[0][0], 45, "Wrong iob count : " + str(count[0][0]))

    def test_set_old_injs_to_fail(self):
        self.cloop_db.clear_db()
        self.cloop_db.execute(
            "insert into injections (datetime_intended, units_intended, injection_type, status) values "
            "(now() - interval 5 minute, 2.0, 'bolus', 'delivered'), "
            "(now() - interval 30 minute, 2.0, 'square', 'delivered')")
        self.confirm.set_old_injs_to_fail()
        inj = self.cloop_db.select("select status from injections where injection_type='bolus'")
        self.assertEqual(inj[0][0], 'delivered', "Wrong status for recent : " + inj[0][0])
        inj = self.cloop_db.select("select status from injections where injection_type='square'")
        self.assertEqual(inj[0][0], 'failed - not confirmed', "Wrong status for old : " + inj[0][0])

    def test_get_max_intervals(self):
        self.cloop_db.clear_db()
        max_square, max_bolus = self.confirm.get_max_intervals()
        self.assertEqual(max_bolus, 220, "Wrong max bolus : " + str(max_bolus))
        self.assertEqual(max_square, 235, "Wrong max square : " + str(max_square))

    def test_set_iob_bg(self):
        self.cloop_db.clear_db()
        self.confirm.save_or_update_injection(self.records[0], None)
        self.confirm.update_iob()
        self.confirm.set_iob_bg()
        count = self.cloop_db.select("select max(iob_bg) from iob")
        self.assertEqual(count[0][0], 150, "Wrong iob_bg max : " + str(count[0][0]))