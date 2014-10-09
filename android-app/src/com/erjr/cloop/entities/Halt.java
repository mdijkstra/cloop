/**
 * 
 */
package com.erjr.cloop.entities;

import java.util.Date;
import java.util.HashMap;

import com.erjr.cloop.main.Util;

/**
 * @author erobinson
 * 
 */
public class Halt {
	public static final String TABLE_HALTS = "halts";
	public static final String ROW_DESC = "halt";
	public static final String COL_HALT_ID = "halt_id";
	public static final String COL_DATETIME_ISSUED = "datetime_issued";
	public static final String COL_TRANSFERRED = "transferred";

	public static final String TABLE_CREATE = "create table " + TABLE_HALTS
			+ "(" + COL_HALT_ID + " integer primary key autoincrement, "
			+ COL_DATETIME_ISSUED + " text not null, " + COL_TRANSFERRED
			+ " text not null);";

	public static String[] allColumns = { COL_HALT_ID, COL_DATETIME_ISSUED,
			COL_TRANSFERRED };

	private long haltID;
	private Date datetimeIssued;
	private String transferred;

	public String getUpdateSql() {
		String sql = " update " + TABLE_HALTS + " set " + COL_DATETIME_ISSUED
				+ " = '" + Util.convertDateToString(datetimeIssued) + "', "
				+ COL_TRANSFERRED + " = '" + transferred + "' where "
				+ COL_HALT_ID + " = " + haltID + ";";
		return sql;
	}

	public String toString() {
		return "#" + haltID + " issued at : "
				+ Util.convertDateToPrettyString(datetimeIssued);
	}

	public String toXML() {
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put(COL_HALT_ID, Integer.toString((int) haltID));
		fields.put(COL_DATETIME_ISSUED,
				Util.convertDateToString(datetimeIssued));
		return Util.rowToXml(ROW_DESC, fields);
	}

	/**
	 * @return the haltID
	 */
	public long getHaltID() {
		return haltID;
	}

	/**
	 * @param haltID
	 *            the haltID to set
	 */
	public void setHaltID(long haltID) {
		this.haltID = haltID;
	}

	/**
	 * @return the datetimeIssued
	 */
	public Date getDatetimeIssued() {
		return datetimeIssued;
	}

	/**
	 * @param datetimeIssued
	 *            the datetimeIssued to set
	 */
	public void setDatetimeIssued(Date datetimeIssued) {
		this.datetimeIssued = datetimeIssued;
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
