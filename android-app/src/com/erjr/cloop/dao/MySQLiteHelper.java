package com.erjr.cloop.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.erjr.cloop.entities.Alert;
import com.erjr.cloop.entities.Automode;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Halt;
import com.erjr.cloop.entities.IOB;
import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.entities.LogRecord;
import com.erjr.cloop.entities.SGV;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "cloop.db";
	private static final int DATABASE_VERSION = 19;
	private static MySQLiteHelper mInstance = null;

	public static MySQLiteHelper getInstance(Context ctx) {

	    // Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (mInstance == null) {
	      mInstance = new MySQLiteHelper(ctx.getApplicationContext());
	    }
	    return mInstance;
	  }

	  /**
	   * Constructor should be private to prevent direct instantiation.
	   * make call to static factory method "getInstance()" instead.
	   */
	  private MySQLiteHelper(Context ctx) {
	    super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	
//	public MySQLiteHelper(Context context) {
//		
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		// TODO: Convert to Course.onUpgrade(db, oldVersion, newVersion)
		dropTables(db);
		onCreate(db);
	}
	
	public void createTables(SQLiteDatabase db) {
		db.execSQL(Alert.TABLE_CREATE);
		db.execSQL(Automode.TABLE_CREATE);
		db.execSQL(Course.TABLE_CREATE);
		db.execSQL(Halt.TABLE_CREATE);
		db.execSQL(Injection.TABLE_CREATE);
		db.execSQL(IOB.TABLE_CREATE);
		db.execSQL(LogRecord.TABLE_CREATE);
		db.execSQL(SGV.TABLE_CREATE);
	}
	
	public void dropTables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + Alert.TABLE_ALERT);
		db.execSQL("DROP TABLE IF EXISTS " + Automode.TABLE_AUTOMODE);
		db.execSQL("DROP TABLE IF EXISTS " + Course.TABLE_COURSES);
		db.execSQL("DROP TABLE IF EXISTS " + Injection.TABLE_INJECTIONS);
		db.execSQL("DROP TABLE IF EXISTS " + IOB.TABLE_IOB);
		db.execSQL("DROP TABLE IF EXISTS " + Halt.TABLE_HALTS);
		db.execSQL("DROP TABLE IF EXISTS " + LogRecord.TABLE_LOG);
		db.execSQL("DROP TABLE IF EXISTS " + SGV.TABLE_SGVS);
	}

}