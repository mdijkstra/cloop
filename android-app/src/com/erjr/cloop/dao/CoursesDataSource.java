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
	private String[] allColumns = { Course.COLUMN_ID,
			Course.COLUMN_CARBS, Course.COLUMN_DATETIME,
			Course.COLUMN_TRANSFERED };

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
		values.put(Course.COLUMN_CARBS, carbs);
		values.put(Course.COLUMN_DATETIME,
				MyDateUtil.convertDateToString(MyDateUtil.getCurrentDateTime()));
		values.put(Course.COLUMN_TRANSFERED, "no");
		long insertId = database.insert(Course.TABLE_COURSES, null,
				values);
		Cursor cursor = database.query(Course.TABLE_COURSES,
				allColumns, Course.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		return newCourse;
	}

	public void deleteComment(Course comment) {
		long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(Course.TABLE_COURSES, Course.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Course> getAllComments() {
		List<Course> comments = new ArrayList<Course>();

		Cursor cursor = database.query(Course.TABLE_COURSES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Course comment = cursorToCourse(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Course cursorToCourse(Cursor cursor) {
		Course course = new Course();
		course.setId(cursor.getLong(0));
		course.setCarbs(cursor.getInt(1));
		course.setDatetime(MyDateUtil.convertStringToDate(cursor.getString(2)));
		course.setTransfered(cursor.getString(3));
		return course;
	}
}