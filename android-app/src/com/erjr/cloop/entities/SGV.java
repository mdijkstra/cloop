/**
 * 
 */
package com.erjr.cloop.entities;

import java.util.Date;

import android.util.Log;

import com.erjr.diabetesi1.Util;

/**
 * @author erobinson
 * 
 */
public class SGV {
	public static final String TABLE_SGVS = "sgvs";
	public static final String ROW_DESC = "sgv_record";
	public static final String COL_SGV_ID = "sgv_id";
	public static final String COL_DEVICE_ID = "device_id";
	public static final String COL_DATETIME_RECORDED = "datetime_recorded";
	public static final String COL_SGV = "sgv";
	public static final String TAG = "SGV";

	public static final String TABLE_CREATE = "create table "
			+ TABLE_SGVS + "(" + COL_SGV_ID
			+ " integer primary key, " + COL_DEVICE_ID + " integer not null, "
			+ COL_DATETIME_RECORDED + " text not null, " + COL_SGV
			+ " int not null);";
	
	public static final String[] allColumns = { COL_SGV_ID, COL_DEVICE_ID,
			COL_DATETIME_RECORDED, COL_SGV };

	private long sgvID;
	private Integer deviceID;
	private Date datetimeRecorded;
	private Integer sgv;

	public void setFromXML(String xml) {
		String sgv_id_str = Util.getValueFromXml(xml,
				COL_SGV_ID);
		String device_id_str = Util
				.getValueFromXml(xml, COL_DEVICE_ID);
		
		Log.i(TAG, "sgv_id: "+sgv_id_str + " device_id: "+device_id_str);
		
		
		this.sgvID = new Integer(sgv_id_str);
		this.deviceID = new Integer(device_id_str);
		this.datetimeRecorded = Util.convertStringToDate(Util.getValueFromXml(
				xml, COL_DATETIME_RECORDED));
		this.sgv = new Integer(Util.getValueFromXml(xml, COL_SGV));
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_SGVS + " ("
				+ COL_SGV_ID + ", " + COL_DEVICE_ID + ", "
				+ COL_DATETIME_RECORDED + ", " + COL_SGV + ") values ("
				+ sgvID + ", " + deviceID + ", '" + Util.convertDateToString(datetimeRecorded) + "', "
				+ sgv + ")";
	}

	public String getDBUpdateSql() {
		return null;
	}

	public String toString() {
		return sgv.toString() + " at "
				+ Util.convertDateToString(datetimeRecorded);
	}

	/**
	 * @return the cgmDataID
	 */
	public long getCgmDataID() {
		return sgvID;
	}

	/**
	 * @param cgmDataID
	 *            the cgmDataID to set
	 */
	public void setCgmDataID(long cgmDataID) {
		this.sgvID = cgmDataID;
	}

	/**
	 * @return the deviceID
	 */
	public Integer getDeviceID() {
		return deviceID;
	}

	/**
	 * @param deviceID
	 *            the deviceID to set
	 */
	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
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
	 * @return the bg
	 */
	public Integer getSg() {
		return sgv;
	}

	/**
	 * @param bg
	 *            the bg to set
	 */
	public void setSg(Integer sg) {
		this.sgv = sg;
	}
}
