package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.IOBDataSource;
import com.erjr.cloop.main.Util;

public class IOB {

	public static final String TABLE_IOB = "iob";
	public static final String ROW_DESC = "iob_record";
	public static final String COL_DATETIME_IOB = "datetime_iob";
	public static final String COL_IOB = "iob";
	public static final String COL_IOB_BG = "iob_bg";

	public static final String TABLE_CREATE = "create table " + TABLE_IOB + "("
			+ COL_DATETIME_IOB + " text primary key not null, " + COL_IOB
			+ " real, " + COL_IOB_BG + " int);";

	public static String[] allColumns = { COL_DATETIME_IOB, COL_IOB, COL_IOB_BG };

	private Date datetimeIOB;
	private Float iob;
	private int iobBg;

	public static void importXml(String iobXml, Context context) {
		if (iobXml == null || iobXml.length() <= 0) {
			return;
		}
		String[] iobXmls = Util.getValuesFromXml(iobXml, ROW_DESC);
		IOBDataSource IOBDS = new IOBDataSource(context);
		for (String iob : iobXmls) {
			IOB i = new IOB();
			i.setFromXml(iob);
			IOBDS.saveIOB(i);
		}
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_IOB + " (" + COL_DATETIME_IOB
				+ ", " + COL_IOB + ", " + COL_IOB_BG + ") values ('"
				+ Util.convertDateToString(datetimeIOB) + "', " + iob + ", "
				+ iobBg + ")";
	}

	public void setFromXml(String xml) {
		datetimeIOB = Util.convertStringToDate(Util.getValueFromXml(xml,
				COL_DATETIME_IOB));
		iob = Util.nullOrFloat(Util.getValueFromXml(xml, COL_IOB));
		iobBg = Util.nullOrInteger(Util.getValueFromXml(xml, COL_IOB_BG));
	}

	public String toString() {
		return "IOB at " + Util.convertDateToPrettyString(datetimeIOB)
				+ " is : " + iob + "u = " + iobBg + " bg";
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

	public int getIobBg() {
		return iobBg;
	}

	public void setIobBg(int iobBg) {
		this.iobBg = iobBg;
	}

}
