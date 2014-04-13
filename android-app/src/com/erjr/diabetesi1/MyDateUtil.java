package com.erjr.diabetesi1;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateUtil {

	public static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
	public static Date convertStringToDate(String string) {
		DateFormat df = new SimpleDateFormat(dateFormat);

		// Get the date today using Calendar object.
		// Date d = Calendar.getInstance().getTime();

		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		Date reportDate;
		try {
			reportDate = df.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return reportDate;
	}

	public static String convertDateToString(Date date) {
		// Create an instance of SimpleDateFormat used for formatting
		// the string representation of date (month/day/year)
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
}
