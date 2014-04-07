package com.erjr.cloop.entities;

import java.util.Date;

import com.erjr.diabetesi1.MyDateUtil;

public class Course {
	public static final String TABLE_COURSES = "courses";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CARBS = "carbs";
	public static final String COLUMN_DATETIME = "datetime";
	public static final String COLUMN_TRANSFERED = "transfered";

	public static final String COURSES_CREATE = "create table "
			+ TABLE_COURSES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_CARBS
			+ " integer not null, " + COLUMN_DATETIME + " text not null, "
			+ COLUMN_TRANSFERED + " text not null);";
	
	private long id;
	private int carbs;
	private Date datetime;
	private String transfered;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return MyDateUtil.convertDateToString(datetime) + " - "
				+ Integer.toString(carbs) + " - " + transfered;
	}

	public int getCarbs() {
		return carbs;
	}

	public void setCarbs(int carbs) {
		this.carbs = carbs;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public String getTransfered() {
		return transfered;
	}

	public void setTransfered(String transfered) {
		this.transfered = transfered;
	}

	public String toXML() {
		return "<course><id>" + id + "</id><carbs>" + carbs
				+ "</carbs><datetime>"
				+ MyDateUtil.convertDateToString(datetime)
				+ "</datetime></course>";
	}
}