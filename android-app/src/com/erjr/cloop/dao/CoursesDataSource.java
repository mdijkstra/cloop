package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.List;

import com.erjr.cloop.entities.Course;
import com.erjr.diabetesi1.MyDateUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CoursesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { Course.COLUMN_COURSE_ID,
			Course.COLUMN_FOOD_ID, Course.COLUMN_SERV_QUANTITY,
			Course.COLUMN_CARBS, Course.COLUMN_DATETIME_CONSUMPTION,
			Course.COLUMN_DATETIME_IDEAL_INJECTION, Course.COLUMN_TRANSFERED };

	public CoursesDataSource(Context context) {
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
		values.put(Course.COLUMN_FOOD_ID, 0);
		values.put(Course.COLUMN_SERV_QUANTITY, 0);
		values.put(Course.COLUMN_CARBS, carbs);
		values.put(Course.COLUMN_DATETIME_CONSUMPTION,
				MyDateUtil.convertDateToString(MyDateUtil.getCurrentDateTime()));
		values.put(Course.COLUMN_DATETIME_IDEAL_INJECTION,
				MyDateUtil.convertDateToString(MyDateUtil.getCurrentDateTime()));
		values.put(Course.COLUMN_TRANSFERED, "no");
		long insertId = database.insert(Course.TABLE_COURSES, null, values);
		Cursor cursor = database.query(Course.TABLE_COURSES, allColumns,
				Course.COLUMN_COURSE_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		return newCourse;
	}

	public void deleteComment(Course comment) {
		long id = comment.getCourseID();
		System.out.println("Comment deleted with id: " + id);
		database.delete(Course.TABLE_COURSES, Course.COLUMN_COURSE_ID + " = "
				+ id, null);
	}

	public List<Course> getAllCourses() {
		List<Course> courses = new ArrayList<Course>();

		Cursor cursor = database.query(Course.TABLE_COURSES, allColumns, null,
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