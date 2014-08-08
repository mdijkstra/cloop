package com.erjr.cloop.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.erjr.cloop.entities.LogRecord;

public class LogDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public LogDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveLogRec(LogRecord logRec) {
		database.execSQL(logRec.getSQLToSave());
	}
}
