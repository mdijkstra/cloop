package com.erjr.cloop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Course;
import com.erjr.diabetesi1.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class CoursesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Context context;

	public CoursesDataSource(Context context) {
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

	public Course createCourse(int carbs) {
		ContentValues values = new ContentValues();
		values.put(Course.COL_FOOD_ID, 0);
		values.put(Course.COL_SERV_QUANTITY, 0);
		values.put(Course.COL_CARBS, carbs);
		values.put(Course.COL_DATETIME_CONSUMPTION,
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Course.COL_DATETIME_IDEAL_INJECTION,
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Course.COL_TRANSFERRED, "no");
		long insertId = database.insert(Course.TABLE_COURSES, null, values);
		Cursor cursor = database
				.query(Course.TABLE_COURSES, Course.allColumns,
						Course.COL_COURSE_ID + " = " + insertId, null, null,
						null, null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		toastNewCourse(newCourse);
		return newCourse;
	}

	public void deleteCourse(Course course) {
		long id = course.getCourseID();
		System.out.println("Course deleted with id: " + id);
		database.delete(Course.TABLE_COURSES,
				Course.COL_COURSE_ID + " = " + id, null);
		Toast.makeText(
				context,
				"Deleted course id " + course.getId() + " of "
						+ course.getCarbs() + "g.", Toast.LENGTH_LONG).show();
	}

	public List<Course> getAllCourses() {
		List<Course> courses = new ArrayList<Course>();

		Cursor cursor = database.query(Course.TABLE_COURSES, Course.allColumns,
				null, null, null, null, null);

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
		database.execSQL("update courses set transferred = 'transferring' where transferred = 'no'");

		Cursor cursor = database.query(Course.TABLE_COURSES, Course.allColumns,
				" transferred = 'transferring' ", null, null, null, null);
		if (cursor.getCount() <= 0) {
			return null;
		}
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

	public void setTransferSuccessful() {
		database.execSQL("update courses set transferred = 'yes' where transferred = 'transferring'");
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
		course.setComment(cursor.getString(6));
		course.setTransferred(cursor.getString(7));
		return course;
	}

	public void saveCourse(Course c) {
		database.execSQL(c.getUpdateSql());
	}

	public Course createCourse(int carbs, Date timeToConsume, String comment) {
		ContentValues values = new ContentValues();
		values.put(Course.COL_CARBS, carbs);
		values.put(Course.COL_COMMENT, comment);
		values.put(Course.COL_DATETIME_CONSUMPTION,
				Util.convertDateToString(timeToConsume));

		values.put(Course.COL_FOOD_ID, 0);
		values.put(Course.COL_SERV_QUANTITY, 0);
		values.put(Course.COL_DATETIME_IDEAL_INJECTION,
				Util.convertDateToString(Util.getCurrentDateTime()));
		values.put(Course.COL_TRANSFERRED, "no");

		long insertId = database.insert(Course.TABLE_COURSES, null, values);
		Cursor cursor = database
				.query(Course.TABLE_COURSES, Course.allColumns,
						Course.COL_COURSE_ID + " = " + insertId, null, null,
						null, null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		toastNewCourse(newCourse);
		return newCourse;
	}

	private void toastNewCourse(Course course) {
		String toastText = "Added meal "
				+ course.getId()
				+ ". Aim to eat "
				+ course.getCarbs()
				+ "g at "
				+ Util.convertDateToPrettyString(course
						.getDatetime_consumption());
		Util.toast(context, toastText);
	}

	public void deleteLastCourse() {
		List<Course> courses = getAllCourses();
		Course course = courses.get(courses.size() - 1);
		deleteCourse(course);
	}

	public List<Course> getCoursesByDateRange(Date startTime, Date endTime) {
		List<Course> courses = new ArrayList<Course>();

		String start = Util.convertDateToString(startTime);
		String end = Util.convertDateToString(endTime);
		String restriction = Course.COL_DATETIME_CONSUMPTION + "> '" + start
				+ "' AND " + Course.COL_DATETIME_CONSUMPTION + " < '" + end
				+ "' ";
		Cursor cursor = database.query(Course.TABLE_COURSES, Course.allColumns,
				restriction, null, null, null, Course.COL_DATETIME_CONSUMPTION
						+ " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Course course = cursorToCourse(cursor);
			courses.add(course);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		if (courses.isEmpty()) {
			return null;
		}
		return courses;
	}
}