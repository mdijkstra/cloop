package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.List;

import com.erjr.cloop.entities.SGV;
import com.erjr.cloop.entities.Course;
import com.erjr.diabetesi1.Util;

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
		dbHelper = new MySQLiteHelper(context);
		open();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// public Course createCourse(int carbs) {
	// ContentValues values = new ContentValues();
	// values.put(Course.COL_FOOD_ID, 0);
	// values.put(Course.COL_SERV_QUANTITY, 0);
	// values.put(Course.COL_CARBS, carbs);
	// values.put(Course.COL_DATETIME_CONSUMPTION,
	// Util.convertDateToString(Util.getCurrentDateTime()));
	// values.put(Course.COL_DATETIME_IDEAL_INJECTION,
	// Util.convertDateToString(Util.getCurrentDateTime()));
	// values.put(Course.COL_TRANSFERED, "no");
	// long insertId = database.insert(Course.TABLE_COURSES, null, values);
	// Cursor cursor = database.query(Course.TABLE_COURSES, allColumns,
	// Course.COL_COURSE_ID + " = " + insertId, null, null, null,
	// null);
	// cursor.moveToFirst();
	// Course newCourse = cursorToCourse(cursor);
	// cursor.close();
	// return newCourse;
	// }

	public SGV getLatestSGV() {
		// Cursor cursor = database.query(Course.TABLE_COURSES, allColumns,
		// null,
		// null, null, null, CGMDataPoint.COL_CGM_DATA_ID);
		Cursor cursor = database.query(SGV.TABLE_SGVS,
				SGV.allColumns, null, null, null, null,
				SGV.COL_SGV_ID, "1");
		if(cursor.getCount() <=0) {
			return null;
		}
		cursor.moveToFirst();
		return cursorToCGMDataPoint(cursor);
	}

	public void saveSGV(SGV cgm) {
		database.execSQL(cgm.getSQLToSave());
	}
	
	private SGV cursorToCGMDataPoint(Cursor cursor) {
		SGV cgm = new SGV();
		cgm.setCgmDataID(cursor.getInt(0));
		cgm.setDeviceID(cursor.getInt(1));
		cgm.setDatetimeRecorded(Util.convertStringToDate(cursor.getString(2)));
		cgm.setSg(cursor.getInt(3));
		return cgm;
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
	// if (course.getTransfered().equals("no")) {
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
	// course.setTransfered(cursor.getString(6));
	// return course;
	// }

	public void saveCourse(Course c) {
		database.execSQL(c.getUpdateSql());
	}
}