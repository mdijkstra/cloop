package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.main.Util;

public class InjectionDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public InjectionDataSource(Context context) {
		dbHelper = MySQLiteHelper.getInstance(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveInjection(Injection inj) {
		database.execSQL(inj.getSQLToSave());
	}

	public List<Injection> getInjectionsByDateRange(Date startTime, Date endTime) {
		List<Injection> injections = new ArrayList<Injection>();

		String start = Util.convertDateToString(startTime);
		String end = Util.convertDateToString(endTime);
		String restriction = Injection.COL_DATETIME_DELIVERED + "> '" + start
				+ "' AND " + Injection.COL_DATETIME_DELIVERED + " < '" + end
				+ "' ";
		Cursor cursor = database.query(Injection.TABLE_INJECTIONS, Injection.allColumns,
				restriction, null, null, null, Injection.COL_DATETIME_DELIVERED
						+ " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Injection injection = cursorToInjection(cursor);
			injections.add(injection);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (injections.isEmpty()) {
			return null;
		}
		return injections;
	}

	private Injection cursorToInjection(Cursor cursor) {
		Injection i = new Injection();
		i.setInjectionId(cursor.getInt(0));
		i.setUnitsIntended(cursor.getFloat(1));
		i.setUnitsDelivered(cursor.getFloat(2));
		i.setTempRate(cursor.getFloat(3));
		i.setDatetimeIntended(Util.convertStringToDate(cursor.getString(4)));
		i.setDatetimeDelivered(Util.convertStringToDate(cursor.getString(5)));
		i.setCurIobUnits(cursor.getFloat(6));
		i.setCurBgUnits(cursor.getFloat(7));
		i.setCorrectionUnits(cursor.getFloat(8));
		i.setCarbsToCover(cursor.getInt(9));
		i.setCarbsUnits(cursor.getFloat(10));
		i.setCurBasalUnits(cursor.getFloat(11));
		i.setAllMealCarbsAbsorbed(cursor.getInt(12));
		i.setStatus(cursor.getString(13));
		return i;
	}
}
