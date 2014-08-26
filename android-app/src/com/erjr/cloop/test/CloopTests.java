/**
 * 
 */
package com.erjr.cloop.test;

import com.erjr.cloop.dao.MySQLiteHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author erobinson
 * 
 *         This class will manage and run tests against the Closed Loop App.
 */
public class CloopTests {
	protected Context ctx;
	private MySQLiteHelper sql;
	private SQLiteDatabase db;

	public CloopTests(Context ctx) {
		this.ctx = ctx;
		sql = MySQLiteHelper.getInstance(ctx);
		db = sql.getWritableDatabase();
	}

	public void clearDB() {
		sql.dropTables(db);
		sql.createTables(db);
	}

	public void runAllTests() {
		BTSyncTest btTest = new BTSyncTest(ctx);
		boolean successful = btTest.runTests();
		if (!successful) {
			return;
		}
	}

}
