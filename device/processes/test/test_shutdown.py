from unittest import TestCase
from device.processes.shutdown_process import Shutdown
from device.processes import cloop_db
__author__ = 'erobinson'


class TestShutdown(TestCase):
    shutdown = Shutdown()
    cloop_db = cloop_db.CloopDB()

    def test_run(self):
        self.cloop_db.execute("delete from halts")
        self.cloop_db.execute("insert into halts (halt_id, datetime_issued) values (1, '2014-08-17T15:32:25')")
        self.shutdown.run()