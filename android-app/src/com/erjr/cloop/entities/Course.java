package com.erjr.cloop.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
	public static final String COL_TRANSFERRED = "transferred";
	public static final String COL_COMMENT = "comment";

	public static final String TABLE_CREATE = "create table " + TABLE_COURSES
			+ "(" + COL_COURSE_ID + " integer primary key autoincrement, "
			+ COL_FOOD_ID + " integer, " + COL_SERV_QUANTITY + " real, "
			+ COL_CARBS + " int not null, " + COL_DATETIME_CONSUMPTION
			+ " text not null, " + COL_DATETIME_IDEAL_INJECTION + " text, "
			+ COL_COMMENT + " text, " + COL_TRANSFERRED + " text not null);";

	public static String[] allColumns = { COL_COURSE_ID, COL_FOOD_ID,
			COL_SERV_QUANTITY, COL_CARBS, COL_DATETIME_CONSUMPTION,
			COL_DATETIME_IDEAL_INJECTION, COL_COMMENT, COL_TRANSFERRED };

	private long courseID;
	private Integer foodID;
	private Float servQuantity;
	private Integer carbs;
	private Date datetimeConsumption;
	private Date datetimeIdealInjection;
	private String comment;
	private String transferred;

	public String getUpdateSql() {
		String sql = " update " + TABLE_COURSES + " set " + COL_FOOD_ID + "="
				+ foodID.toString() + ", " + COL_SERV_QUANTITY + " = "
				+ servQuantity.toString() + ", " + COL_CARBS + " = "
				+ carbs.toString() + ", " + COL_CARBS + " = '"
				+ Util.convertDateToString(datetimeConsumption) + "', "
				+ COL_DATETIME_CONSUMPTION + " = '"
				+ Util.convertDateToString(datetimeIdealInjection) + "', "
				+ COL_TRANSFERRED + " = '" + transferred + "', " + COL_COMMENT
				+ " = '" + comment + "' where " + COL_COURSE_ID + " = "
				+ courseID + ";";
		return sql;
	}

	public String toString() {
		return "ID (" + courseID + ") food-" + foodID.toString() + " * "
				+ servQuantity.toString() + " = " + carbs.toString()
				+ " eaten at " + Util.convertDateToPrettyString(datetimeConsumption)
				+ " thus should inject at "
				+ Util.convertDateToPrettyString(datetimeIdealInjection) + " ("
				+ comment + "). Transferred = " + transferred;
	}

	public String toXML() {
		// TODO: Convert to using column names instead of hard coded strings
//		return "<" + ROW_DESC + "><" + COL_COURSE_ID + ">" + courseID + "</"
//				+ COL_COURSE_ID + "><" + COL_FOOD_ID + ">" + foodID + "</"
//				+ COL_FOOD_ID + "><" + COL_SERV_QUANTITY + ">" + servQuantity
//				+ "</" + COL_SERV_QUANTITY + "><" + COL_CARBS + ">" + carbs
//				+ "</" + COL_CARBS + "><" + COL_DATETIME_CONSUMPTION + ">"
//				+ Util.convertDateToString(datetimeConsumption) + "</"
//				+ COL_DATETIME_CONSUMPTION + "><"
//				+ COL_DATETIME_IDEAL_INJECTION + ">"
//				+ Util.convertDateToString(datetimeConsumption) + "</"
//				+ COL_DATETIME_IDEAL_INJECTION + "><" + COL_COMMENT + ">"
//				+ comment + "</" + COL_COMMENT + "></" + ROW_DESC + ">";
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put(COL_COURSE_ID, Integer.toString((int) courseID));
		fields.put(COL_FOOD_ID, foodID.toString());
		fields.put(COL_SERV_QUANTITY, servQuantity.toString());
		fields.put(COL_CARBS, carbs.toString());
		fields.put(COL_DATETIME_CONSUMPTION, Util.convertDateToString(datetimeConsumption));
		fields.put(COL_DATETIME_IDEAL_INJECTION, Util.convertDateToString(datetimeIdealInjection));
		fields.put(COL_COMMENT, comment);
		return rowToXml(ROW_DESC, fields);
	}
	
	public String rowToXml(String rowDescriptor, HashMap<String, String> fields) {
		if(rowDescriptor == null || fields == null) {
			return "";
		}
		String xml = "<"+rowDescriptor+">";
		Iterator<Entry<String, String>> it = fields.entrySet().iterator();
	    while (it.hasNext()) {
	        Entry<String, String> pairs = it.next();
	        //System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        xml += "<"+pairs.getKey()+">"+pairs.getValue()+"</"+pairs.getKey()+">";
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    return xml + "</"+rowDescriptor+">";
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