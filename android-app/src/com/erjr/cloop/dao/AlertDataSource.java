package com.erjr.cloop.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.erjr.cloop.entities.Alert;
import com.erjr.cloop.entities.SGV;
import com.erjr.diabetesi1.Util;

public class AlertDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public AlertDataSource(Context context) {
		dbHelper = MySQLiteHelper.getInstance(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void saveAlert(Alert a) {
		database.execSQL(a.getSQLToSave());
	}

	public Alert[] getAlertsToShow() {
		int alertsFromPastMinutes = 30;
		Date currentDate = Util.getCurrentDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.MINUTE, -alertsFromPastMinutes);
		String dateLimit = Util.convertDateToString(c.getTime());
		Cursor cursor = database.query(Alert.TABLE_ALERT, Alert.allColumns,
				Alert.COL_DATETIME_TO_ALERT + " > '" + dateLimit
						+ "' and datetime_dismissed is null", null, null, null,
				Alert.COL_DATETIME_TO_ALERT + " DESC", null);
		if (cursor.getCount() <= 0) {
			return null;
		}
		Alert[] alerts = new Alert[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			alerts[alerts.length - i - 1] = cursorToAlert(cursor);
		}

		cursor.close();
		return alerts;
	}

	public Alert getAlert(int alertId) {
		Cursor cursor = database.query(Alert.TABLE_ALERT, Alert.allColumns,
				Alert.COL_ALERT_ID + " = " + alertId, null, null, null,
				Alert.COL_DATETIME_TO_ALERT + " DESC", null);
		if (cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToNext();
		Alert a = cursorToAlert(cursor);
		cursor.close();
		return a;
	}

	private Alert cursorToAlert(Cursor cursor) {
		Alert a = new Alert();
		a.setAlertId(cursor.getInt(0));
		a.setDatetimeRecorded(Util.convertStringToDate(cursor.getString(1)));
		a.setDatetimeToAlert(Util.convertStringToDate(cursor.getString(2)));
		a.setSrc(cursor.getString(3));
		a.setCode(cursor.getString(4));
		a.setType(cursor.getString(5));
		a.setTitle(cursor.getString(6));
		a.setMessage(cursor.getString(7));
		a.setValue(cursor.getString(8));
		a.setOption1(cursor.getString(9));
		a.setOption2(cursor.getString(10));
		return a;
	}

	public void dismiss(int alertId) {
		Alert a = getAlert(alertId);
		a.setDatetimeDismissed(Util.getCurrentDateTime());
		a.setSrcDismissed("phone");
		saveAlert(a);
	}
}
