package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.erjr.cloop.entities.SGV;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.main.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SGVDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public SGVDataSource(Context context) {
		dbHelper = MySQLiteHelper.getInstance(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public SGV getLatestSGV() {
		Cursor cursor = database.query(SGV.TABLE_SGVS, SGV.allColumns, null,
				null, null, null, SGV.COL_DATETIME_RECORDED + " DESC", "1");
		if (cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();
		SGV sgv = cursorToSGV(cursor);
		cursor.close();
		return sgv;
	}

	public SGV[] getRecentSGVs(int hours) {
		Date currentDate = Util.getCurrentDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.HOUR, -hours);
		String dateLimit = Util.convertDateToString(c.getTime());
		Cursor cursor = database.query(SGV.TABLE_SGVS, SGV.allColumns,
				SGV.COL_DATETIME_RECORDED + " > '" + dateLimit + "'", null,
				null, null, SGV.COL_DATETIME_RECORDED + " DESC",
				Integer.toString(hours * 12)); // 12 data points per hour thus
												// limit to hour * 12
		if (cursor.getCount() <= 0) {
			return null;
		}
		SGV[] sgvs = new SGV[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			sgvs[sgvs.length - i - 1] = cursorToSGV(cursor);
		}

		cursor.close();
		return sgvs;
	}

	public SGV[] getSGVToSendToCloud() {
		Cursor cursor = database.query(SGV.TABLE_SGVS, SGV.allColumns,
				SGV.COL_IN_CLOUD + " != 'yes'", null, null, null,
				SGV.COL_DATETIME_RECORDED + " ASC", null);
		if (cursor.getCount() <= 0) {
			return null;
		}
		SGV[] sgvs = new SGV[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			sgvs[sgvs.length - i - 1] = cursorToSGV(cursor);
		}
		cursor.close();
		return sgvs;
	}

	public void saveSGV(SGV sgv) {
		database.execSQL(sgv.getSQLToSave());
	}

	private SGV cursorToSGV(Cursor cursor) {
		SGV cgm = new SGV();
		cgm.setCgmDataID(cursor.getInt(0));
		cgm.setDeviceID(cursor.getInt(1));
		cgm.setDatetimeRecorded(Util.convertStringToDate(cursor.getString(2)));
		cgm.setSg(cursor.getInt(3));
		return cgm;
	}

	public Integer getCount() {
		// TODO Auto-generated method stub
		
		return null;
	}

	// public List<Course> getCoursesToTransfer() {
	// List<Course> courses = new ArrayList<Course>();
	//
	// Cursor cursor = database.query(Course.TABLE_COURSES, allColumns, null,
	// null, null, null, null);
	//
	// cursor.moveToFirst();
	// while (!cursor.isAfterLast()) {
	// Course course = cursorToCourse(cursor);
	// if (course.getTransferred().equals("no")) {
	// courses.add(course);
	// } // TODO: just add where clause to database.query call above.
	// cursor.moveToNext();
	// }
	// // make sure to close the cursor
	// cursor.close();
	// return courses;
	// }
	//
	// private Course cursorToCourse(Cursor cursor) {
	// Course course = new Course();
	// course.setCourseID(cursor.getLong(0));
	// course.setFoodID(cursor.getInt(1));
	// course.setServQuantity(cursor.getFloat(2));
	// course.setCarbs(cursor.getInt(3));
	// course.setDatetimeConsumption(Util.convertStringToDate(cursor
	// .getString(4)));
	// course.setDatetimeIdealInjection(Util.convertStringToDate(cursor
	// .getString(5)));
	// course.setTransferred(cursor.getString(6));
	// return course;
	// }
}