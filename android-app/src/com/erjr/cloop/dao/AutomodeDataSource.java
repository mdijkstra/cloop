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
import com.erjr.cloop.main.Util;

public class AutomodeDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Context context;

	public AutomodeDataSource(Context context) {
		this.context = context;
		dbHelper = MySQLiteHelper.getInstance(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Automode createAutomode(String isOn) {
		ContentValues values = new ContentValues();
		values.put(Automode.COL_DATETIME_RECORDED,
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Automode.COL_IS_ON, isOn);
		values.put(Automode.COL_TRANSFERRED, "no");
		long insertId = database.insert(Automode.TABLE_AUTOMODE, null, values);
		Cursor cursor = database.query(Automode.TABLE_AUTOMODE,
				Automode.allColumns, Automode.COL_AUTOMODE_ID + " = "
						+ insertId, null, null, null, null);
		cursor.moveToFirst();
		Automode a = cursorToAutomode(cursor);
		cursor.close();
		return a;
	}

	private Automode cursorToAutomode(Cursor cursor) {
		Automode a = new Automode();
		a.setAutomodeSwitchId(cursor.getInt(0));
		a.setDatetimeRecorded(Util.convertStringToDate(cursor.getString(1)));
		a.setIsOn(cursor.getString(2));
		a.setTransferred(cursor.getString(3));
		return a;
	}

	public Automode getLatestAutomode() {
		Cursor c = database
				.query(Automode.TABLE_AUTOMODE, Automode.allColumns, null,
						null, null, null, Automode.COL_AUTOMODE_ID + " DESC",
						"1");
		if(c.getCount() <= 0) {
			return null;
		}
		c.moveToFirst();
		return cursorToAutomode(c);
	}

	public List<Automode> getAutomodesToTransfer() {
		List<Automode> automodes = new ArrayList<Automode>();
		database.execSQL("update " + Automode.TABLE_AUTOMODE
				+ " set transferred = 'transferring' where transferred = 'no'");

		Cursor cursor = database.query(Automode.TABLE_AUTOMODE,
				Automode.allColumns, " transferred = 'transferring' ", null,
				null, null, null);
		if (cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Automode automode = cursorToAutomode(cursor);
			automodes.add(automode);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return automodes;
	}

	public void setTransferSuccessful() {
		database.execSQL("update " + Automode.TABLE_AUTOMODE
				+ " set transferred = 'yes' where transferred = 'transferring'");
	}

	public List<Automode> getAutosByDateRange(Date startTime, Date endTime) {
		List<Automode> Automodes = new ArrayList<Automode>();

		String start = Util.convertDateToString(startTime);
		String end = Util.convertDateToString(endTime);
		String restriction = Automode.COL_DATETIME_RECORDED+ "> '" + start
				+ "' AND " + Automode.COL_DATETIME_RECORDED + " < '" + end
				+ "' ";
		Cursor cursor = database.query(Automode.TABLE_AUTOMODE, Automode.allColumns,
				restriction, null, null, null, Automode.COL_DATETIME_RECORDED
						+ " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Automode course = cursorToAutomode(cursor);
			Automodes.add(course);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (Automodes.isEmpty()) {
			return null;
		}
		return Automodes;
	}
}
