package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.IOBDataSource;
import com.erjr.cloop.dao.LogDataSource;
import com.erjr.diabetesi1.Util;

public class LogRecord {

	public static final String TABLE_LOG = "log";
	public static final String ROW_DESC = "log_record";
	public static final String COL_LOG_ID = "log_id";
	public static final String COL_SRC_DEVICE = "src_device";
	public static final String COL_DATETIME_LOGGED = "datetime_logged";
	public static final String COL_CODE = "code";
	public static final String COL_TYPE = "type";
	public static final String COL_MESSAGE = "message";
	public static final String COL_OPTION1 = "option1";
	public static final String COL_OPTION2 = "option2";

	public static final String TABLE_CREATE = "create table " + TABLE_LOG + "("
			+ COL_LOG_ID + " Integer primary key not null, " + COL_SRC_DEVICE
			+ " text, " + COL_DATETIME_LOGGED + " text, " + COL_CODE
			+ " text, " + COL_TYPE + " text, " + COL_MESSAGE + " text, "
			+ COL_OPTION1 + " text, " + COL_OPTION2 + " text);";

	public static String[] allColumns = { COL_LOG_ID, COL_SRC_DEVICE,
			COL_DATETIME_LOGGED, COL_CODE, COL_TYPE, COL_MESSAGE, COL_OPTION1,
			COL_OPTION2 };

	private Integer logId;
	private String srcDevice;
	private Date datetimeLogged;
	private String code;
	private String type;
	private String message;
	private String option1;
	private String option2;

	public static void importXml(String logXml, Context context) {
		if (logXml == null || logXml.length() <= 0) {
			return;
		}
		String[] logXmls = Util.getValuesFromXml(logXml, ROW_DESC);
		LogDataSource LogDS= new LogDataSource(context);
		for (String rec : logXmls) {
			LogRecord l = new LogRecord();
			l.setFromXml(rec);
			LogDS.saveLogRec(l);
		}
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_LOG + " (" + COL_LOG_ID + ", "
				+ COL_SRC_DEVICE + ", " + COL_DATETIME_LOGGED + ", " + COL_CODE
				+ ", " + COL_TYPE + ", " + COL_MESSAGE + ", " + COL_OPTION1
				+ ", " + COL_OPTION2 + ") values (" + logId + ", " + srcDevice
				+ ", " + Util.convertDateToString(datetimeLogged) + ", " + code
				+ ", " + type + ", " + message + ", " + option1 + ", "
				+ option2 + ")";
	}

	public void setFromXml(String xml) {
		logId = new Integer(Util.getValueFromXml(xml, "log_id"));
		srcDevice = Util.getValueFromXml(xml, "src_device");
		datetimeLogged = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_logged"));
		code = Util.getValueFromXml(xml, "code");
		type = Util.getValueFromXml(xml, "type");
		message = Util.getValueFromXml(xml, "message");
		option1 = Util.getValueFromXml(xml, "option1");
		option2 = Util.getValueFromXml(xml, "option2");

	}

	public String toString() {
		return "Log #" + logId + " at "
				+ Util.convertDateToPrettyString(datetimeLogged) + " from "
				+ srcDevice + ":" + code + " says: " + message;
	}

}
