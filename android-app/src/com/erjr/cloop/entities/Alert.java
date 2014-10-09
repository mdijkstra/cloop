package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.AlertDataSource;
import com.erjr.cloop.main.Util;

public class Alert {

	public static final String TABLE_ALERT = "alerts";
	public static final String ROW_DESC = "alert";

	public static final String COL_ALERT_ID = "alert_id";
	public static final String COL_DATETIME_RECORDED = "datetime_recorded";
	public static final String COL_DATETIME_TO_ALERT = "datetime_to_alert";
	public static final String COL_SRC = "src";
	public static final String COL_CODE = "code";
	public static final String COL_TYPE = "type";
	public static final String COL_TITLE = "title";
	public static final String COL_MESSAGE = "message";
	public static final String COL_VALUE = "value";
	public static final String COL_OPTION1 = "option1";
	public static final String COL_OPTION2 = "option2";
	public static final String COL_DATETIME_DISMISSED = "datetime_dismissed";
	public static final String COL_SRC_DISMISSED = "src_dismissed";

	public static final String TABLE_CREATE = "create table " + TABLE_ALERT
			+ "(" + COL_ALERT_ID + " Integer primary key not null,"
			+ COL_DATETIME_RECORDED + " text, " + COL_DATETIME_TO_ALERT
			+ " text, " + COL_SRC + " text, " + COL_CODE + " text, " + COL_TYPE
			+ " text, " + COL_TITLE + " text," + COL_MESSAGE + " text, "
			+ COL_VALUE + " text, " + COL_OPTION1 + " text, " + COL_OPTION2
			+ " text, " + COL_DATETIME_DISMISSED + " text, "
			+ COL_SRC_DISMISSED + " text);";

	public static String[] allColumns = { COL_ALERT_ID, COL_DATETIME_RECORDED,
			COL_DATETIME_TO_ALERT, COL_SRC, COL_CODE, COL_TYPE, COL_TITLE,
			COL_MESSAGE, COL_VALUE, COL_OPTION1, COL_OPTION2,
			COL_DATETIME_DISMISSED, COL_SRC_DISMISSED };

	private Integer alertId;
	private Date datetimeRecorded;
	private Date datetimeToAlert;
	private String src;
	private String code;
	private String type;
	private String title;
	private String message;
	private String value;
	private String option1;
	private String option2;
	private Date datetimeDismissed;
	private String srcDismissed;

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
		// don't update the dismiss values to null in case set by dismissing the
		// alert
		String dismissColumns = "," + COL_DATETIME_DISMISSED + ","
				+ COL_SRC_DISMISSED;
		String dismissValues = "', "
				+ Util.sqlSafeField(Util.convertDateToString(datetimeDismissed))
				+ ", " + Util.sqlSafeField(srcDismissed);
		if (datetimeDismissed == null) {
			dismissColumns = "";
			dismissValues = "'";
		}

		return "INSERT OR REPLACE INTO " + TABLE_ALERT + " (" + COL_ALERT_ID
				+ "," + COL_DATETIME_RECORDED + "," + COL_DATETIME_TO_ALERT
				+ "," + COL_SRC + "," + COL_CODE + "," + COL_TYPE + ","
				+ COL_TITLE + "," + COL_MESSAGE + "," + COL_VALUE + ","
				+ COL_OPTION1 + "," + COL_OPTION2 + dismissColumns
				+ ") values (" + alertId + ",'"
				+ Util.convertDateToString(datetimeRecorded) + "','"
				+ Util.convertDateToString(datetimeToAlert) + "','" + src
				+ "','" + code + "','" + type + "','"+title+"','" + message
				+ "','" + value + "','" + option1 + "','" + option2
				+ dismissValues + ")";
	}

	public void setFromXml(String xml) {
		alertId = Util.nullOrInteger(Util.getValueFromXml(xml, "alert_id"));
		datetimeRecorded = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_recorded"));
		datetimeToAlert = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_to_alert"));
		src = Util.getValueFromXml(xml, "src");
		code = Util.getValueFromXml(xml, "code");
		type = Util.getValueFromXml(xml, "type");
		title = Util.getValueFromXml(xml, "title");
		message = Util.getValueFromXml(xml, "message");
		value = Util.getValueFromXml(xml, "value");
		option1 = Util.getValueFromXml(xml, "option1");
		option2 = Util.getValueFromXml(xml, "option2");
		datetimeDismissed = Util.convertStringToDate(Util.getValueFromXml(xml,
				COL_DATETIME_DISMISSED));
		srcDismissed = Util.getValueFromXml(xml, COL_SRC_DISMISSED);
	}

	public String toString() {
		return "Alert (" + type + ") " + message + " from " + src + " for "
				+ Util.convertDateToPrettyString(datetimeToAlert);
	}

	/**
	 * @return the alertId
	 */
	public Integer getAlertId() {
		return alertId;
	}

	/**
	 * @param alertId
	 *            the alertId to set
	 */
	public void setAlertId(Integer alertId) {
		this.alertId = alertId;
	}

	/**
	 * @return the datetimeRecorded
	 */
	public Date getDatetimeRecorded() {
		return datetimeRecorded;
	}

	/**
	 * @param datetimeRecorded
	 *            the datetimeRecorded to set
	 */
	public void setDatetimeRecorded(Date datetimeRecorded) {
		this.datetimeRecorded = datetimeRecorded;
	}

	/**
	 * @return the datetimeToAlert
	 */
	public Date getDatetimeToAlert() {
		return datetimeToAlert;
	}

	/**
	 * @param datetimeToAlert
	 *            the datetimeToAlert to set
	 */
	public void setDatetimeToAlert(Date datetimeToAlert) {
		this.datetimeToAlert = datetimeToAlert;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src
	 *            the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the option1
	 */
	public String getOption1() {
		return option1;
	}

	/**
	 * @param option1
	 *            the option1 to set
	 */
	public void setOption1(String option1) {
		this.option1 = option1;
	}

	/**
	 * @return the option2
	 */
	public String getOption2() {
		return option2;
	}

	/**
	 * @param option2
	 *            the option2 to set
	 */
	public void setOption2(String option2) {
		this.option2 = option2;
	}

	/**
	 * @return the datetimeDismissed
	 */
	public Date getDatetimeDismissed() {
		return datetimeDismissed;
	}

	/**
	 * @param datetimeDismissed
	 *            the datetimeDismissed to set
	 */
	public void setDatetimeDismissed(Date datetimeDismissed) {
		this.datetimeDismissed = datetimeDismissed;
	}

	/**
	 * @return the srcDismissed
	 */
	public String getSrcDismissed() {
		return srcDismissed;
	}

	/**
	 * @param srcDismissed
	 *            the srcDismissed to set
	 */
	public void setSrcDismissed(String srcDismissed) {
		this.srcDismissed = srcDismissed;
	}
}