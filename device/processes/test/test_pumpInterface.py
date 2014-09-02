from unittest import TestCase
from device.processes import pump_interface

__author__ = 'erobinson'


class TestPumpInterface(TestCase):
    pump_interface = pump_interface.PumpInterface()

    def test_parse_json(self):
        json_str = '[{\
                "programmed": 1.0,\
                "_type": "Bolus",\
                "_description": "Bolus 2014-08-31T20:14:33 head[4], body[0] op[0x01]",\
                "duration": 0,\
                "timestamp": "2014-08-31T20:14:33",\
                "type": "normal",\
                "amount": 1.0 \
            }]'
        obj = self.pump_interface.parse_json(json_str)
        o = obj[0]

        self.assertEquals(o["type"], "normal", "Wrong type")
        self.assertEquals(o["_type"], "Bolus", "Wrong type")
        self.assertEquals(o["timestamp"], "2014-08-31T20:14:33", "Wrong time")