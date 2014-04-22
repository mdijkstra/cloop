package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.List;

import com.erjr.cloop.entities.CGMDataPoint;
import com.erjr.cloop.entities.Course;
import com.erjr.diabetesi1.MyDateUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CGMDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { Course.COL_COURSE_ID,
			Course.COL_FOOD_ID, Course.COL_SERV_QUANTITY,
			Course.COL_CARBS, Course.COL_DATETIME_CONSUMPTION,
			Course.COL_DATETIME_IDEAL_INJECTION, Course.COL_TRANSFERED };

	public CGMDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Course createCourse(int carbs) {
		ContentValues values = new ContentValues();
		values.put(Course.COL_FOOD_ID, 0);
		values.put(Course.COL_SERV_QUANTITY, 0);
		values.put(Course.COL_CARBS, carbs);
		values.put(Course.COL_DATETIME_CONSUMPTION,
				MyDateUtil.convertDateToString(MyDateUtil.getCurrentDateTime()));
		values.put(Course.COL_DATETIME_IDEAL_INJECTION,
				MyDateUtil.convertDateToString(MyDateUtil.getCurrentDateTime()));
		values.put(Course.COL_TRANSFERED, "no");
		long insertId = database.insert(Course.TABLE_COURSES, null, values);
		Cursor cursor = database.query(Course.TABLE_COURSES, allColumns,
				Course.COL_COURSE_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		return newCourse;
	}

	public CGMDataPoint getCurrentBG() {
		//TODO: query the db for the max cgm if > 30 min return null for not available
		return null;
	}
	
	public List<Course> getCoursesToTransfer() {
		List<Course> courses = new ArrayList<Course>();

		Cursor cursor = database.query(Course.TABLE_COURSES, allColumns, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Course course = cursorToCourse(cursor);
			if (course.getTransfered().equals("no")) {
				courses.add(course);
			} // TODO: just add where clause to database.query call above.
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return courses;
	}

	private Course cursorToCourse(Cursor cursor) {
		Course course = new Course();
		course.setCourseID(cursor.getLong(0));
		course.setFoodID(cursor.getInt(1));
		course.setServQuantity(cursor.getFloat(2));
		course.setCarbs(cursor.getInt(3));
		course.setDatetimeConsumption(MyDateUtil.convertStringToDate(cursor
				.getString(4)));
		course.setDatetimeIdealInjection(MyDateUtil.convertStringToDate(cursor
				.getString(5)));
		course.setTransfered(cursor.getString(6));
		return course;
	}

	public void saveCourse(Course c) {
		database.execSQL(c.getUpdateSql());
	}
}