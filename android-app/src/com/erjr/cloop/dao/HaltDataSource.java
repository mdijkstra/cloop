package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.erjr.cloop.entities.Automode;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Halt;
import com.erjr.cloop.entities.SGV;
import com.erjr.cloop.main.Util;

public class HaltDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public HaltDataSource(Context context) {
		dbHelper = MySQLiteHelper.getInstance(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Halt createHalt() {
		ContentValues values = new ContentValues();
		values.put(Halt.COL_DATETIME_ISSUED,
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Automode.COL_TRANSFERRED, "no");
		long insertId = database.insert(Halt.TABLE_HALTS, null, values);
		Cursor cursor = database.query(Halt.TABLE_HALTS, Halt.allColumns,
				Halt.COL_HALT_ID + " = " + insertId, null, null, null, null);
		if(cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();
		Halt h = cursorToHalt(cursor);
		cursor.close();
		return h;
	}

	public List<Halt> getHaltsToTransfer() {
		List<Halt> halts = new ArrayList<Halt>();
		database.execSQL("update " + Halt.TABLE_HALTS
				+ " set transferred = 'transferring' where transferred = 'no'");

		Cursor cursor = database.query(Halt.TABLE_HALTS, Halt.allColumns,
				" transferred = 'transferring' ", null, null, null, null);
		if (cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Halt automode = cursorToHalt(cursor);
			halts.add(automode);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return halts;
	}

	public Halt getLatestHalt() {
		Cursor cursor = database.query(Halt.TABLE_HALTS, Halt.allColumns, null,
				null, null, null, Halt.COL_HALT_ID + " DESC", "1");
		if (cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();
		return cursorToHalt(cursor);
	}

	public void saveSGV(SGV cgm) {
		database.execSQL(cgm.getSQLToSave());
	}

	private Halt cursorToHalt(Cursor cursor) {
		Halt h = new Halt();
		h.setHaltID(cursor.getInt(0));
		h.setDatetimeIssued(Util.convertStringToDate(cursor.getString(1)));
		h.setTransferred(cursor.getString(2));
		return h;
	}

	public void saveCourse(Course c) {
		database.execSQL(c.getUpdateSql());
	}
	
	public void setTransferSuccessful() {
		database.execSQL("update " + Halt.TABLE_HALTS
				+ " set transferred = 'yes' where transferred = 'transferring'");
	}

	public List<Halt> getHaltsByDateRange(Date startTime,
			Date endTime) {
		List<Halt> halts = new ArrayList<Halt>();

		String start = Util.convertDateToString(startTime);
		String end = Util.convertDateToString(endTime);
		String restriction = Halt.COL_DATETIME_ISSUED+ "> '" + start
				+ "' AND " + Halt.COL_DATETIME_ISSUED + " < '" + end
				+ "' ";
		Cursor cursor = database.query(Halt.TABLE_HALTS, Halt.allColumns,
				restriction, null, null, null, Halt.COL_DATETIME_ISSUED
						+ " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Halt course = cursorToHalt(cursor);
			halts.add(course);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (halts.isEmpty()) {
			return null;
		}
		return halts;
	}
}