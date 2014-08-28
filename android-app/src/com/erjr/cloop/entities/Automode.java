package com.erjr.cloop.entities;

import java.util.Date;
import java.util.HashMap;

import android.database.Cursor;

import com.erjr.diabetesi1.Util;

public class Automode {

	public static final String TABLE_AUTOMODE = "automode_switch";
	public static final String ROW_DESC = "automode";

	public static final String COL_AUTOMODE_ID = "automode_switch_id";
	public static final String COL_DATETIME_RECORDED = "datetime_recorded";
	public static final String COL_IS_ON = "is_on";
	public static final String COL_TRANSFERRED = "transferred";

	public static final String TABLE_CREATE = "create table " + TABLE_AUTOMODE
			+ " (" + COL_AUTOMODE_ID + " integer primary key autoincrement,"
			+ COL_DATETIME_RECORDED + " text, " + COL_IS_ON + " text, "
			+ COL_TRANSFERRED + " text);";

	public static String[] allColumns = { COL_AUTOMODE_ID,
			COL_DATETIME_RECORDED, COL_IS_ON, COL_TRANSFERRED };

	private Integer automodeSwitchId;
	private Date datetimeRecorded;
	private String isOn;
	private String transferred;

	public String getUpdateSql() {
		String sql = " update " + TABLE_AUTOMODE + " set "
				+ COL_DATETIME_RECORDED + " = '"
				+ Util.convertDateToString(datetimeRecorded) + "', "
				+ COL_IS_ON + " = '" + isOn + "', " + COL_TRANSFERRED + " = '"
				+ transferred + "' where " + COL_AUTOMODE_ID + " = "
				+ automodeSwitchId;
		return sql;
	}

	public String toString() {
		String str = "";
		str = "Automode is " + getIsOn();
		str += " at " + Util.convertDateToPrettyString(datetimeRecorded)
				+ " id(" + automodeSwitchId + ").";
		return str;
	}

	public String toXML() {
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put(COL_AUTOMODE_ID, Integer.toString((int) automodeSwitchId));
		fields.put(COL_DATETIME_RECORDED,
				Util.convertDateToString(datetimeRecorded));
		fields.put(COL_IS_ON, isOn);
		return Util.rowToXml(ROW_DESC, fields);
	}

	/**
	 * @return the automodeSwitchId
	 */
	public Integer getAutomodeSwitchId() {
		return automodeSwitchId;
	}

	/**
	 * @param automodeSwitchId
	 *            the automodeSwitchId to set
	 */
	public void setAutomodeSwitchId(Integer automodeSwitchId) {
		this.automodeSwitchId = automodeSwitchId;
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
	 * @return the isOn
	 */
	public String getIsOn() {
		return isOn;
	}

	/**
	 * @param isOn
	 *            the isOn to set
	 */
	public void setIsOn(String isOn) {
		this.isOn = isOn;
	}

	/**
	 * @return the transferred
	 */
	public String getTransferred() {
		return transferred;
	}

	/**
	 * @param transferred
	 *            the transferred to set
	 */
	public void setTransferred(String transferred) {
		this.transferred = transferred;
	}
}
