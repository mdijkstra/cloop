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
public class Halt {
	public static final String TABLE_HALTS = "halts";
	public static final String ROW_DESC = "halt_record";
	public static final String COL_HALT_ID = "halt_id";
	public static final String COL_DATETIME_ISSUED = "datetime_issued";
	public static final String COL_TRANSFERED = "transfered";

	public static final String TABLE_CREATE = "create table "
			+ TABLE_HALTS + "(" + COL_HALT_ID
			+ " integer primary key autoincrement, " + COL_DATETIME_ISSUED
			+ " text not null, " + COL_TRANSFERED + " text not null);";

	public static String[] allColumns = { COL_HALT_ID, COL_DATETIME_ISSUED,
			COL_TRANSFERED };

	private long haltID;
	private Date datetimeIssued;
	private String transfered;

	public String getUpdateSql() {
		String sql = " update " + TABLE_HALTS + " set " + COL_DATETIME_ISSUED
				+ " = '" + Util.convertDateToString(datetimeIssued) + "', "
				+ COL_TRANSFERED + " = '" + transfered + "' where "
				+ COL_HALT_ID + " = " + haltID + ";";
		return sql;
	}

	public String toString() {
		return "ID (" + haltID + ") issued at : "
				+ Util.convertDateToString(datetimeIssued) + ". Transfered = "
				+ transfered;
	}

	public String toXML() {
		// TODO: Convert to using column names instead of hard coded strings
		return "<" + ROW_DESC + "><" + COL_HALT_ID + ">" + haltID + "</"
				+ COL_HALT_ID + "><"+COL_DATETIME_ISSUED+">"
				+ Util.convertDateToString(datetimeIssued)
				+ "</"+COL_DATETIME_ISSUED+"></" + ROW_DESC + ">";
	}

	/**
	 * @return the haltID
	 */
	public long getHaltID() {
		return haltID;
	}

	/**
	 * @param haltID the haltID to set
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
	 * @param datetimeIssued the datetimeIssued to set
	 */
	public void setDatetimeIssued(Date datetimeIssued) {
		this.datetimeIssued = datetimeIssued;
	}

	/**
	 * @return the transfered
	 */
	public String getTransfered() {
		return transfered;
	}

	/**
	 * @param transfered the transfered to set
	 */
	public void setTransfered(String transfered) {
		this.transfered = transfered;
	}

}