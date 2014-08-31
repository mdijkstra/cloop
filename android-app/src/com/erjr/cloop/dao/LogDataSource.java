package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.LogRecord;
import com.erjr.cloop.main.Util;

public class LogDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public LogDataSource(Context context) {
		dbHelper = MySQLiteHelper.getInstance(context);
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

	public List<LogRecord> getTodaysLogs() {
		Date currentDate = Util.getCurrentDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.HOUR, -24);
		return getLogsByDateRange(c.getTime(), Util.getCurrentDateTime());
	}

	public List<LogRecord> getLogsByDateRange(Date startTime, Date endTime) {
		List<LogRecord> logs = new ArrayList<LogRecord>();

		String start = Util.convertDateToString(startTime);
		String end = Util.convertDateToString(endTime);
		String restriction = LogRecord.COL_DATETIME_LOGGED + "> '" + start
				+ "' AND " + LogRecord.COL_DATETIME_LOGGED + " < '" + end
				+ "' ";
		Cursor cursor = database.query(LogRecord.TABLE_LOG,
				LogRecord.allColumns, restriction, null, null, null,
				LogRecord.COL_DATETIME_LOGGED + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LogRecord log = cursorToLog(cursor);
			logs.add(log);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if(logs.isEmpty()) {
			return null;
		}
		return logs;
	}

	private LogRecord cursorToLog(Cursor cursor) {
		LogRecord log = new LogRecord();
		log.setLogId(cursor.getInt(0));
		log.setSrcDevice(cursor.getString(1));
		log.setDatetimeLogged(Util.convertStringToDate(cursor.getString(2)));
		log.setCode(cursor.getString(3));
		log.setType(cursor.getString(4));
		log.setMessage(cursor.getString(5));
		log.setOption1(cursor.getString(6));
		log.setOption2(cursor.getString(7));
		return log;
	}
}
