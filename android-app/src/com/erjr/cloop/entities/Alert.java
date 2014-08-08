package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.AlertDataSource;
import com.erjr.diabetesi1.Util;

public class Alert {

	public static final String TABLE_ALERT = "alerts";
	public static final String ROW_DESC = "alert";

	public static final String COL_ALERT_ID = "alert_id";
	public static final String COL_DATETIME_RECORDED = "datetime_recorded";
	public static final String COL_DATETIME_TO_ALERT = "datetime_to_alert";
	public static final String COL_SRC = "src";
	public static final String COL_CODE = "code";
	public static final String COL_TYPE = "type";
	public static final String COL_MESSAGE = "message";
	public static final String COL_VALUE = "value";
	public static final String COL_OPTION1 = "option1";
	public static final String COL_OPTION2 = "option2";

	public static final String TABLE_CREATE = "create table " + TABLE_ALERT
			+ "(" + COL_ALERT_ID + " Integer primary key not null,"
			+ COL_DATETIME_RECORDED + " text, " + COL_DATETIME_TO_ALERT
			+ " text, " + COL_SRC + " text, " + COL_CODE + " text, " + COL_TYPE
			+ " text, " + COL_MESSAGE + " text, " + COL_VALUE + " text, "
			+ COL_OPTION1 + " text, " + COL_OPTION2 + " text);";

	public static String[] allColumns = { COL_ALERT_ID, COL_DATETIME_RECORDED,
			COL_DATETIME_TO_ALERT, COL_SRC, COL_CODE, COL_TYPE, COL_MESSAGE,
			COL_VALUE, COL_OPTION1, COL_OPTION2 };

	private Integer alertId;
	private Date datetimeRecorded;
	private Date datetimeToAlert;
	private String src;
	private String code;
	private String type;
	private String message;
	private String value;
	private String option1;
	private String option2;

	public static void importXml(String alertsXml, Context context) {
		if (alertsXml == null || alertsXml.length() <= 0) {
			return;
		}
		String[] alertXmls = Util.getValuesFromXml(alertsXml, ROW_DESC);
		AlertDataSource alertDS = new AlertDataSource(context);
		for (String rec : alertXmls) {
			Alert a = new Alert();
			a.setFromXml(rec);
			alertDS.saveAlert(a);
		}
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_ALERT + " (" + COL_ALERT_ID
				+ "," + COL_DATETIME_RECORDED + "," + COL_DATETIME_TO_ALERT
				+ "," + COL_SRC + "," + COL_CODE + "," + COL_TYPE + ","
				+ COL_MESSAGE + "," + COL_VALUE + "," + COL_OPTION1 + ","
				+ COL_OPTION2 + ") values (" + alertId + ",'"
				+ Util.convertDateToString(datetimeRecorded) + "','"
				+ Util.convertDateToString(datetimeToAlert) + "','" + src + "','"
				+ code + "','" + type + "','" + message + "','" + value + "','"
				+ option1 + "','" + option2 + "')";
	}

	public void setFromXml(String xml) {
		alertId = new Integer(Util.getValueFromXml(xml, "alert_id"));
		datetimeRecorded = Util.convertStringToDate(Util.getValueFromXml(xml, "datetime_recorded"));
		datetimeToAlert = Util.convertStringToDate(Util.getValueFromXml(xml, "datetime_to_alert"));
		src = Util.getValueFromXml(xml, "src");
		code = Util.getValueFromXml(xml, "code");
		type = Util.getValueFromXml(xml, "type");
		message = Util.getValueFromXml(xml, "message");
		value = Util.getValueFromXml(xml, "value");
		option1 = Util.getValueFromXml(xml, "option1");
		option2 = Util.getValueFromXml(xml, "option2");

	}

	public String toString() {
		return "Alert (" + type + ") " + message + " from " + src + " for "
				+ Util.convertDateToPrettyString(datetimeToAlert);
	}
}