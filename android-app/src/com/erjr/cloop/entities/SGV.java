/**
 * 
 */
package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.erjr.cloop.dao.SGVDataSource;
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
	public static final String COL_IN_CLOUD = "in_cloud";
	public static final String TAG = "SGV";

	public static final String TABLE_CREATE = "create table " + TABLE_SGVS
			+ "(" + COL_SGV_ID + " integer primary key, " + COL_DEVICE_ID
			+ " integer not null, " + COL_DATETIME_RECORDED
			+ " text not null, " + COL_SGV + " int not null, " + COL_IN_CLOUD
			+ " text not null);";

	public static final String[] allColumns = { COL_SGV_ID, COL_DEVICE_ID,
			COL_DATETIME_RECORDED, COL_SGV, COL_IN_CLOUD };

	private long sgvID;
	private Integer deviceID;
	private Date datetimeRecorded;
	private Integer sgv;
	private String inCloud = "no";

	public void setFromXml(String xml) {
		String sgv_id_str = Util.getValueFromXml(xml, COL_SGV_ID);
		String device_id_str = Util.getValueFromXml(xml, COL_DEVICE_ID);

		Log.i(TAG, "sgv_id: " + sgv_id_str + " device_id: " + device_id_str);

		this.sgvID = Util.nullOrInteger(sgv_id_str);
		this.deviceID = Util.nullOrInteger(device_id_str);
		this.datetimeRecorded = Util.convertStringToDate(Util.getValueFromXml(
				xml, COL_DATETIME_RECORDED));
		this.sgv = Util.nullOrInteger(Util.getValueFromXml(xml, COL_SGV));
		this.inCloud = "no";
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_SGVS + " (" + COL_SGV_ID
				+ ", " + COL_DEVICE_ID + ", " + COL_DATETIME_RECORDED + ", "
				+ COL_SGV + "," + COL_IN_CLOUD + ") values (" + sgvID + ", "
				+ deviceID + ", '" + Util.convertDateToString(datetimeRecorded)
				+ "', " + sgv + ",'" + inCloud + "')";
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

	public String getInCloud() {
		return inCloud;
	}

	public void setInCloud(String inCloud) {
		this.inCloud = inCloud;
	}

	public static void importXml(String fullSGVXml, Context context) {
		if (fullSGVXml == null || fullSGVXml.isEmpty()) {
			return;
		}
		String[] sgvsXmlAsArray = Util.getValuesFromXml(fullSGVXml,
				SGV.ROW_DESC);
		SGVDataSource SGVDS = new SGVDataSource(context);
		for (String sgvXml : sgvsXmlAsArray) {
			SGV sgv = new SGV();
			sgv.setFromXml(sgvXml);
			SGVDS.saveSGV(sgv);
		}
	}
}
