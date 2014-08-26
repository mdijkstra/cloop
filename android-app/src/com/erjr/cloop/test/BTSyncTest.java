package com.erjr.cloop.test;

import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.diabetesi1.BTSyncThread;

import android.content.Context;

public class BTSyncTest extends CloopTests {

	public BTSyncTest(Context ctx) {
		super(ctx);
	}

	public boolean runTests() {
		boolean successful = testImports();
		if (!successful)
			return false;
		return true;
	}

	public boolean testImports() {
		String[][] xmls = new String[4][7];
		BTSyncThread btSync = new BTSyncThread(ctx);
		SGVDataSource sgvDS = new SGVDataSource(ctx);
		// xml, number of sgvs, alerts, injections, courses_to_injections, iob,
		// logs
		String[] strings = { null, "0", "0", "0", "0", "0", "0" };
		xmls[0] = strings;
		String[] strings1 = { "", "0", "0", "0", "0", "0", "0" };
		xmls[1] = strings1;
		String[] strings2 = { "</EOM>", "0", "0", "0", "0", "0", "0" };
		xmls[2] = strings2;
		String[] strings3 = { "<sgvs></sgvs>", "0", "0", "0", "0", "0", "0" };
		xmls[3] = strings3;
		for (String[] xml : xmls) {
			clearDB();
			btSync.processDataReceived(xml[0]);
			if(sgvDS.getCount()!=(new Integer(xml[1]))) {
				
			}
		}
		return false;
	}
}
