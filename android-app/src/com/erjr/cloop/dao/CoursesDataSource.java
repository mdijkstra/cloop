package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.List;

import com.erjr.cloop.entities.Course;
import com.erjr.diabetesi1.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CoursesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public CoursesDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
		open();
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
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Course.COL_DATETIME_IDEAL_INJECTION,
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Course.COL_TRANSFERED, "no");
		long insertId = database.insert(Course.TABLE_COURSES, null, values);
		Cursor cursor = database.query(Course.TABLE_COURSES, Course.allColumns,
				Course.COL_COURSE_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		return newCourse;
	}

	public void deleteComment(Course comment) {
		long id = comment.getCourseID();
		System.out.println("Comment deleted with id: " + id);
		database.delete(Course.TABLE_COURSES, Course.COL_COURSE_ID + " = "
				+ id, null);
	}

	public List<Course> getAllCourses() {
		List<Course> courses = new ArrayList<Course>();

		Cursor cursor = database.query(Course.TABLE_COURSES, Course.allColumns, null,
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Course course = cursorToCourse(cursor);
			courses.add(course);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return courses;
	}

	public List<Course> getCoursesToTransfer() {
		List<Course> courses = new ArrayList<Course>();

		Cursor cursor = database.query(Course.TABLE_COURSES, Course.allColumns, null,
				null, null, null, null);
		if(cursor.getCount() <= 0) {
			return null;
		}
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
		course.setDatetimeConsumption(Util.convertStringToDate(cursor
				.getString(4)));
		course.setDatetimeIdealInjection(Util.convertStringToDate(cursor
				.getString(5)));
		course.setTransfered(cursor.getString(6));
		return course;
	}

	public void saveCourse(Course c) {
		database.execSQL(c.getUpdateSql());
	}
}