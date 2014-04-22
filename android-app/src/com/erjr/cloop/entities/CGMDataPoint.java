/**
 * 
 */
package com.erjr.cloop.entities;

import java.util.Date;

/**
 * @author erobinson
 * 
 */
public class CGMDataPoint {
	public static final String TABLE_CGM_DATA_POINT = "cgm_data";
	public static final String COL_CGM_DATA_ID = "cgm_data_id";
	public static final String COL_DEVICE_ID = "device_id";
	public static final String COL_DATETIME_RECORDED = "serv_quantity";
	public static final String COL_BG = "bg";

	public static final String COURSES_CREATE = "create table "
			+ TABLE_CGM_DATA_POINT + "(" + COL_CGM_DATA_ID + " integer, "
			+ COL_DEVICE_ID + " integer not null, " + COL_DATETIME_RECORDED
			+ " text not null, " + COL_BG + " int not null);";
	
	private long cgmDataID;
	private Integer deviceID;
	private Date datetimeRecorded;
	private Integer bg;
	
	public void setFromXML(String xml) {
		
	}
	
	public String getUpdateSql() {
		return null;
	}
	
	public String toString(){
		return null;
	}

	/**
	 * @return the cgmDataID
	 */
	public long getCgmDataID() {
		return cgmDataID;
	}

	/**
	 * @param cgmDataID the cgmDataID to set
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
	 * @param deviceID the deviceID to set
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
	 * @param datetimeRecorded the datetimeRecorded to set
	 */
	public void setDatetimeRecorded(Date datetimeRecorded) {
		this.datetimeRecorded = datetimeRecorded;
	}

	/**
	 * @return the bg
	 */
	public Integer getBg() {
		return bg;
	}

	/**
	 * @param bg the bg to set
	 */
	public void setBg(Integer bg) {
		this.bg = bg;
	}
}
