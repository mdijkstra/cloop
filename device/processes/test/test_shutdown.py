from unittest import TestCase
from device.processes.shutdown_process import Shutdown

__author__ = 'erobinson'


class TestShutdown(TestCase):
    shutdown = Shutdown()

    def test_run(self):
        self.shutdown.db.execute("delete from halts")
        self.shutdown.db.execute("insert into halts (halt_id, datetime_issued) values (1, '2014-08-17T15:32:25')")
        self.shutdown.run()