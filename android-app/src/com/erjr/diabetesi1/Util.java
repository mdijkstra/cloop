package com.erjr.diabetesi1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import android.content.Context;
import android.widget.Toast;

public class Util {

	public static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	public static Date convertStringToDate(String string) {
		if (string == null || string.isEmpty()
				|| string.equalsIgnoreCase("null")
				|| string.equalsIgnoreCase("None")) {
			return null;
		}
		DateFormat df = new SimpleDateFormat(dateFormat);

		// Get the date today using Calendar object.
		// Date d = Calendar.getInstance().getTime();

		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		Date reportDate;
		try {
			reportDate = df.parse(string);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return reportDate;
	}

	public static String convertDateToString(Date date) {
		// Create an instance of SimpleDateFormat used for formatting
		// the string representation of date (month/day/year)
		if (date == null) {
			return "null";
		}
		DateFormat df = new SimpleDateFormat(dateFormat);

		// Get the date today using Calendar object.
		// Date d = Calendar.getInstance().getTime();

		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		String reportDate = df.format(date);

		return reportDate;
	}

	public static Date getCurrentDateTime() {
		Date d = Calendar.getInstance().getTime();
		return d;
	}

	public static String getValueFromXml(String xml, String tag) {
		if (xml == null) {
			return null;
		}
		int start = xml.indexOf("<" + tag + ">") + tag.length() + 2;
		int end = xml.indexOf("</" + tag + ">");
		if (start == end) {
			return "";
		}
		String str = xml.substring(start, end);
		if (str.equals("None") || str.equals("null")) {
			return null;
		} else {
			return str;
		}
	}

	public static String[] getValuesFromXml(String xml, String tag) {
		LinkedList<String> values = new LinkedList<String>();
		int end = 0;
		while (xml.length() > 2) {
			values.add(getValueFromXml(xml, tag));
			end = xml.indexOf("</" + tag + ">");
			xml = xml.substring(end + tag.length() + 3);
		}
		return (String[]) values.toArray(new String[0]);
	}

	public static String convertDateToPrettyString(Date datetimeRecorded) {
		String str = "";
		Date currentDate = getCurrentDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DATE, -1);
		Date yesterday = c.getTime();

		if (yesterday.after(datetimeRecorded)) {
			str += datetimeRecorded.getMonth() + "/"
					+ datetimeRecorded.getDate();
		}
		c.add(Calendar.YEAR, -1);
		Date lastYear = c.getTime();
		if (lastYear.after(datetimeRecorded)) {
			str += "/"
					+ String.valueOf(datetimeRecorded.getYear()).substring(1);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		str += " " + timeFormat.format(datetimeRecorded);
		return str;
	}

	public static Integer nullOrInteger(String integer) {
		if (integer == null || integer.isEmpty() || integer.equals("None")
				|| integer.equals("null")) {
			return null;
		} else {
			return new Integer(integer);
		}
	}

	public static Float nullOrFloat(String fl) {
		if (fl == null || fl.isEmpty() || fl.equals("None")
				|| fl.equals("null")) {
			return null;
		} else {
			return new Float(fl);
		}
	}

	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
