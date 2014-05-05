package com.erjr.cloop.entities;

import java.util.Date;

import com.erjr.diabetesi1.Util;

public class Course {
	public static final String TABLE_COURSES = "courses";
	public static final String ROW_DESC = "course";
	public static final String COL_COURSE_ID = "course_id";
	public static final String COL_FOOD_ID = "food_id";
	public static final String COL_SERV_QUANTITY = "serv_quantity";
	public static final String COL_CARBS = "carbs";
	public static final String COL_DATETIME_CONSUMPTION = "datetime_consumption";
	public static final String COL_DATETIME_IDEAL_INJECTION = "datetime_ideal_injection";
	public static final String COL_TRANSFERED = "transfered";

	public static final String COURSES_CREATE = "create table " + TABLE_COURSES
			+ "(" + COL_COURSE_ID + " integer primary key autoincrement, "
			+ COL_FOOD_ID + " integer, " + COL_SERV_QUANTITY + " real, "
			+ COL_CARBS + " int not null, " + COL_DATETIME_CONSUMPTION
			+ " text not null, " + COL_DATETIME_IDEAL_INJECTION + " text, "
			+ COL_TRANSFERED + " text not null);";

	public static String[] allColumns = { COL_COURSE_ID, COL_FOOD_ID,
			COL_SERV_QUANTITY, COL_CARBS, COL_DATETIME_CONSUMPTION,
			COL_DATETIME_IDEAL_INJECTION, COL_TRANSFERED };

	private long courseID;
	private Integer foodID;
	private Float servQuantity;
	private Integer carbs;
	private Date datetimeConsumption;
	private Date datetimeIdealInjection;
	private String transfered;

	public String getUpdateSql() {
		String sql = " update " + TABLE_COURSES + " set " + COL_FOOD_ID + "="
				+ foodID.toString() + ", " + COL_SERV_QUANTITY + " = "
				+ servQuantity.toString() + ", " + COL_CARBS + " = "
				+ carbs.toString() + ", " + COL_CARBS + " = '"
				+ Util.convertDateToString(datetimeConsumption) + "', "
				+ COL_DATETIME_CONSUMPTION + " = '"
				+ Util.convertDateToString(datetimeIdealInjection) + "', "
				+ COL_TRANSFERED + " = '" + transfered + "' where "
				+ COL_COURSE_ID + " = " + courseID + ";";
		return sql;
	}

	public String toString() {
		return "ID (" + courseID + ") food-" + foodID.toString() + " * "
				+ servQuantity.toString() + " = " + carbs.toString()
				+ " eaten at " + Util.convertDateToString(datetimeConsumption)
				+ " thus should inject at "
				+ Util.convertDateToString(datetimeIdealInjection)
				+ ". Transfered = " + transfered;
	}

	public String toXML() {
		// TODO: Convert to using column names instead of hard coded strings
		return "<" + ROW_DESC + "><" + COL_COURSE_ID + ">" + courseID + "</"
				+ COL_COURSE_ID + "><food_id>" + foodID
				+ "</food_id><serv_quantity>" + servQuantity
				+ "</serv_quantity><carbs>" + carbs
				+ "</carbs><datetime_consumption>"
				+ Util.convertDateToString(datetimeConsumption)
				+ "</datetime_consumption><datetime_ideal_injection>"
				+ Util.convertDateToString(datetimeConsumption)
				+ "</datetime_ideal_injection></"+ROW_DESC+">";
	}

	public long getId() {
		return courseID;
	}

	/**
	 * @return the courseID
	 */
	public long getCourseID() {
		return courseID;
	}

	/**
	 * @param courseID
	 *            the courseID to set
	 */
	public void setCourseID(long courseID) {
		this.courseID = courseID;
	}

	/**
	 * @return the foodID
	 */
	public Integer getFoodID() {
		return foodID;
	}

	/**
	 * @param foodID
	 *            the foodID to set
	 */
	public void setFoodID(Integer foodID) {
		this.foodID = foodID;
	}

	/**
	 * @return the serv_quantity
	 */
	public Float getServQuantity() {
		return servQuantity;
	}

	/**
	 * @param serv_quantity
	 *            the serv_quantity to set
	 */
	public void setServQuantity(Float serv_quantity) {
		this.servQuantity = serv_quantity;
	}

	/**
	 * @return the carbs
	 */
	public Integer getCarbs() {
		return carbs;
	}

	/**
	 * @param carbs
	 *            the carbs to set
	 */
	public void setCarbs(Integer carbs) {
		this.carbs = carbs;
	}

	/**
	 * @return the datetime_consumption
	 */
	public Date getDatetime_consumption() {
		return datetimeConsumption;
	}

	/**
	 * @param datetime_consumption
	 *            the datetime_consumption to set
	 */
	public void setDatetimeConsumption(Date datetime_consumption) {
		this.datetimeConsumption = datetime_consumption;
	}

	/**
	 * @return the datetime_ideal_injection
	 */
	public Date getDatetimeIdealInjection() {
		return datetimeIdealInjection;
	}

	/**
	 * @param datetime_ideal_injection
	 *            the datetime_ideal_injection to set
	 */
	public void setDatetimeIdealInjection(Date datetime_ideal_injection) {
		this.datetimeIdealInjection = datetime_ideal_injection;
	}

	/**
	 * @return the transfered
	 */
	public String getTransfered() {
		return transfered;
	}

	/**
	 * @param transfered
	 *            the transfered to set
	 */
	public void setTransfered(String transfered) {
		this.transfered = transfered;
	}
}