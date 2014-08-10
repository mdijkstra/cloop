package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.IOBDataSource;
import com.erjr.cloop.dao.LogDataSource;
import com.erjr.diabetesi1.Util;

public class LogRecord {

	public static final String TABLE_LOG = "logs";
	public static final String ROW_DESC = "log";
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
		LogDataSource LogDS = new LogDataSource(context);
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
				+ ", " + COL_OPTION2 + ") values (" + logId + ", '" + srcDevice
				+ "', '" + Util.convertDateToString(datetimeLogged) + "', '"
				+ code + "', '" + type + "', '" + message + "', '" + option1
				+ "', '" + option2 + "')";
	}

	public void setFromXml(String xml) {
		logId = Util.nullOrInteger(Util.getValueFromXml(xml, "log_id"));
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
		return (!type.equalsIgnoreCase("success") ? "FAIL " : "")
				+ code.toUpperCase() + " #" + logId + " "
				+ Util.convertDateToPrettyString(datetimeLogged) + " : "
				+ message;
	}

	/**
	 * @return the logId
	 */
	public Integer getLogId() {
		return logId;
	}

	/**
	 * @param logId
	 *            the logId to set
	 */
	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	/**
	 * @return the srcDevice
	 */
	public String getSrcDevice() {
		return srcDevice;
	}

	/**
	 * @param srcDevice
	 *            the srcDevice to set
	 */
	public void setSrcDevice(String srcDevice) {
		this.srcDevice = srcDevice;
	}

	/**
	 * @return the datetimeLogged
	 */
	public Date getDatetimeLogged() {
		return datetimeLogged;
	}

	/**
	 * @param datetimeLogged
	 *            the datetimeLogged to set
	 */
	public void setDatetimeLogged(Date datetimeLogged) {
		this.datetimeLogged = datetimeLogged;
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

}
