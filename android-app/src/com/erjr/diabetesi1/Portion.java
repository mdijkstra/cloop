package com.erjr.diabetesi1;

import java.util.Date;

public class Portion {
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
		return "<portion><id>" + id + "</id><carbs>" + carbs
				+ "</carbs><datetime>"
				+ MyDateUtil.convertDateToString(datetime)
				+ "</datetime></portion>";
	}
}