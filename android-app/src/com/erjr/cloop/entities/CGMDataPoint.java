/**
 * 
 */
package com.erjr.cloop.entities;

import java.util.Date;

import com.erjr.diabetesi1.Util;

/**
 * @author erobinson
 * 
 */
public class CGMDataPoint {
	public static final String TABLE_CGM_DATA_POINT = "cgm_data";
	public static final String ROW_DESC = "cgm_data_point";
	public static final String COL_CGM_DATA_ID = "cgm_data_id";
	public static final String COL_DEVICE_ID = "device_id";
	public static final String COL_DATETIME_RECORDED = "datetime_recorded";
	public static final String COL_SG = "sg";

	public static final String CGM_TABLE_CREATE = "create table "
			+ TABLE_CGM_DATA_POINT + "(" + COL_CGM_DATA_ID
			+ " integer primary key, " + COL_DEVICE_ID + " integer not null, "
			+ COL_DATETIME_RECORDED + " text not null, " + COL_SG
			+ " int not null);";
	
	public static final String[] allColumns = { COL_CGM_DATA_ID, COL_DEVICE_ID,
			COL_DATETIME_RECORDED, COL_SG };

	private long cgmDataID;
	private Integer deviceID;
	private Date datetimeRecorded;
	private Integer sg;

	public void setFromXML(String xml) {
		this.cgmDataID = Integer.valueOf(Util.getValueFromXml(xml,
				COL_CGM_DATA_ID));
		this.deviceID = Integer.valueOf(Util
				.getValueFromXml(xml, COL_DEVICE_ID));
		this.datetimeRecorded = Util.convertStringToDate(Util.getValueFromXml(
				xml, COL_DATETIME_RECORDED));
		this.sg = Integer.valueOf(Util.getValueFromXml(xml, COL_SG));
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_CGM_DATA_POINT + " ("
				+ COL_CGM_DATA_ID + ", " + COL_DEVICE_ID + ", "
				+ COL_DATETIME_RECORDED + ", " + COL_SG + ") values ("
				+ cgmDataID + ", " + deviceID + ", " + datetimeRecorded + ", "
				+ sg + ")";
	}

	public String getDBUpdateSql() {
		return null;
	}

	public String toString() {
		return sg.toString() + " at "
				+ Util.convertDateToString(datetimeRecorded);
	}

	/**
	 * @return the cgmDataID
	 */
	public long getCgmDataID() {
		return cgmDataID;
	}

	/**
	 * @param cgmDataID
	 *            the cgmDataID to set
	 */
	public void setCgmDataID(long cgmDataID) {
		this.cgmDataID = cgmDataID;
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
		return sg;
	}

	/**
	 * @param bg
	 *            the bg to set
	 */
	public void setSg(Integer sg) {
		this.sg = sg;
	}
}
