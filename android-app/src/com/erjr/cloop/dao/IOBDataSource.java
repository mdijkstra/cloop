package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.erjr.cloop.entities.IOB;
import com.erjr.cloop.main.Util;

public class IOBDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public IOBDataSource(Context context) {
		dbHelper = MySQLiteHelper.getInstance(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveIOB(IOB iob) {
		database.execSQL(iob.getSQLToSave());
	}

	public List<IOB> getByDateRange(Date startTime, Date endTime) {
		List<IOB> iobs = new ArrayList<IOB>();

		String start = Util.convertDateToString(startTime);
		String end = Util.convertDateToString(endTime);
		String restriction = IOB.COL_DATETIME_IOB + "> '" + start + "' AND "
				+ IOB.COL_DATETIME_IOB + " < '" + end + "' ";
		Cursor cursor = database.query(IOB.TABLE_IOB, IOB.allColumns,
				restriction, null, null, null, IOB.COL_DATETIME_IOB
						+ " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			IOB course = cursorToIOB(cursor);
			iobs.add(course);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (iobs.isEmpty()) {
			return null;
		}
		return iobs;
	}

	private IOB cursorToIOB(Cursor cursor) {
		IOB iob = new IOB();
		iob.setDatetimeIOB(Util.convertStringToDate(cursor.getString(0)));
		iob.setIob(cursor.getFloat(1));
		iob.setIobBg(cursor.getInt(2));
		return iob;
	}
}
