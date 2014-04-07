package com.erjr.diabetesi1;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PortionsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_CARBS, MySQLiteHelper.COLUMN_DATETIME,
			MySQLiteHelper.COLUMN_TRANSFERED };

	public PortionsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Portion createPortion(int carbs) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CARBS, carbs);
		values.put(MySQLiteHelper.COLUMN_DATETIME,
				MyDateUtil.convertDateToString(MyDateUtil.getCurrentDateTime()));
		values.put(MySQLiteHelper.COLUMN_TRANSFERED, "no");
		long insertId = database.insert(MySQLiteHelper.TABLE_PORTIONS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PORTIONS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Portion newPortion = cursorToPortion(cursor);
		cursor.close();
		return newPortion;
	}

	public void deleteComment(Portion comment) {
		long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_PORTIONS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Portion> getAllComments() {
		List<Portion> comments = new ArrayList<Portion>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_PORTIONS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Portion comment = cursorToPortion(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Portion cursorToPortion(Cursor cursor) {
		Portion portion = new Portion();
		portion.setId(cursor.getLong(0));
		portion.setCarbs(cursor.getInt(1));
		portion.setDatetime(MyDateUtil.convertStringToDate(cursor.getString(2)));
		portion.setTransfered(cursor.getString(3));
		return portion;
	}
}