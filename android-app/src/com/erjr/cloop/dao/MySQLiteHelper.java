package com.erjr.cloop.dao;

import com.erjr.cloop.entities.Alert;
import com.erjr.cloop.entities.Halt;
import com.erjr.cloop.entities.IOB;
import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.entities.LogRecord;
import com.erjr.cloop.entities.SGV;
import com.erjr.cloop.entities.Course;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "cloop.db";
	private static final int DATABASE_VERSION = 13;

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(Alert.TABLE_CREATE);
		database.execSQL(Course.TABLE_CREATE);
		database.execSQL(Halt.TABLE_CREATE);
		database.execSQL(Injection.TABLE_CREATE);
		database.execSQL(IOB.TABLE_CREATE);
		database.execSQL(LogRecord.TABLE_CREATE);
		database.execSQL(SGV.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		// TODO: Convert to Course.onUpgrade(db, oldVersion, newVersion)

		db.execSQL("DROP TABLE IF EXISTS " + Alert.TABLE_ALERT);
		db.execSQL("DROP TABLE IF EXISTS " + Course.TABLE_COURSES);
		db.execSQL("DROP TABLE IF EXISTS " + Injection.TABLE_INJECTIONS);
		db.execSQL("DROP TABLE IF EXISTS " + IOB.TABLE_IOB);
		db.execSQL("DROP TABLE IF EXISTS " + Halt.TABLE_HALTS);
		db.execSQL("DROP TABLE IF EXISTS " + LogRecord.TABLE_LOG);
		db.execSQL("DROP TABLE IF EXISTS " + SGV.TABLE_SGVS);
		onCreate(db);
	}

}