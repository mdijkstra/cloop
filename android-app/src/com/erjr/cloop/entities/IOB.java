package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.IOBDataSource;
import com.erjr.diabetesi1.Util;

public class IOB {

	public static final String TABLE_IOB = "iob";
	public static final String ROW_DESC = "iob_record";
	public static final String COL_DATETIME_IOB = "datetime_iob";
	public static final String COL_IOB = "iob";

	public static final String TABLE_CREATE = "create table " + TABLE_IOB + "("
			+ COL_DATETIME_IOB + " text primary key not null, " + COL_IOB + " real);";

	public static String[] allColumns = { COL_DATETIME_IOB, COL_IOB };

	private Date datetimeIOB;
	private Float iob;

	public static void importXml(String iobXml, Context context) {
		iobXml = Util.getValueFromXml(iobXml, "iobs");
		String[] iobXmls = Util.getValuesFromXml(iobXml, ROW_DESC);
		IOBDataSource IOBDS = new IOBDataSource(context);
		for (String iob : iobXmls) {
			IOB i = new IOB();
			i.setFromXml(iob);
			IOBDS.saveIOB(i);
		}
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_IOB + " ("
				+ COL_DATETIME_IOB + ", " + COL_IOB + ") values ("
				+ Util.convertDateToString(datetimeIOB) + ", " + iob + ")";
	}

	public void setFromXml(String xml) {
		datetimeIOB = Util.convertStringToDate(Util.getValueFromXml(xml,
				COL_DATETIME_IOB));
		iob = new Float(Util.getValueFromXml(xml, COL_IOB));
	}

	public String toString() {
		return "IOB at " + Util.convertDateToPrettyString(datetimeIOB)
				+ " is : " + iob + " units";
	}

	public Date getDatetimeIOB() {
		return datetimeIOB;
	}

	public void setDatetimeIOB(Date datetimeIOB) {
		this.datetimeIOB = datetimeIOB;
	}

	public float getIob() {
		return iob;
	}

	public void setIob(float iob) {
		this.iob = iob;
	}

}
