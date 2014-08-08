package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.diabetesi1.Util;

public class Injection {

	public static final String TABLE_INJECTIONS = "injections";
	public static final String ROW_DESC = "injection";
	public static final String COL_INJECTION_ID = "injection_id";
	public static final String COL_UNITS_INTENDED = "units_intended";
	public static final String COL_UNITS_DELIVERED = "units_delivered";
	public static final String COL_TEMP_RATE = "temp_rate";
	public static final String COL_DATETIME_INTENDED = "datetime_intended";
	public static final String COL_DATETIME_DELIVERED = "datetime_delivered";
	public static final String COL_CUR_IOB_UNITS = "cur_iob_units";
	public static final String COL_CUR_BG_UNITS = "cur_bg_units";
	public static final String COL_CORRECTION_UNITS = "correction_units";
	public static final String COL_CARBS_TO_COVER = "carbs_to_cover";
	public static final String COL_CARBS_UNITS = "carbs_units";
	public static final String COL_CUR_BASAL_UNITS = "cur_basal_units";
	public static final String COL_ALL_MEAL_CARBS_ABSORBED = "all_meal_carbs_absorbed";
	public static final String COL_STATUS = "status";

	public static final String TABLE_CREATE = "create table "
			+ TABLE_INJECTIONS + "(" + COL_INJECTION_ID
			+ " Integer primary key not null, " + COL_UNITS_INTENDED
			+ " real not null, " + COL_UNITS_DELIVERED + " real, "
			+ COL_TEMP_RATE + " real, " + COL_DATETIME_INTENDED + " text, "
			+ COL_DATETIME_DELIVERED + " text, " + COL_CUR_IOB_UNITS
			+ " real, " + COL_CUR_BG_UNITS + " Integer, "
			+ COL_CORRECTION_UNITS + " real, " + COL_CARBS_TO_COVER
			+ " Integer, " + COL_CARBS_UNITS + " real, " + COL_CUR_BASAL_UNITS
			+ " real, " + COL_ALL_MEAL_CARBS_ABSORBED + " Integer, "
			+ COL_STATUS + " text);";
	public static String[] allColumns = { COL_INJECTION_ID, COL_UNITS_INTENDED,
			COL_UNITS_DELIVERED, COL_TEMP_RATE, COL_DATETIME_INTENDED,
			COL_DATETIME_DELIVERED, COL_CUR_IOB_UNITS, COL_CUR_BG_UNITS,
			COL_CORRECTION_UNITS, COL_CARBS_TO_COVER, COL_CARBS_UNITS,
			COL_CUR_BASAL_UNITS, COL_ALL_MEAL_CARBS_ABSORBED, COL_STATUS };

	private Integer injectionId;
	private Float unitsIntended;
	private Float unitsDelivered;
	private Float tempRate;
	private Date datetimeIntended;
	private Date datetimeDelivered;
	private Float curIobUnits;
	private Float curBgUnits;
	private Float correctionUnits;
	private Integer carbsToCover;
	private Float carbsUnits;
	private Float curBasalUnits;
	private boolean allMealCarbsAbsorbed;
	private String status;

	public static void importXml(String injXml, Context context) {
		injXml = Util.getValueFromXml(injXml, "iobs");
		String[] injXmls = Util.getValuesFromXml(injXml, ROW_DESC);
		InjectionDataSource InjDS = new InjectionDataSource(context);
		for (String iob : injXmls) {
			Injection i = new Injection();
			i.setFromXml(iob);
			InjDS.saveInjection(i);
		}
	}

	public String getSQLToSave() {
		return "INSERT OR REPLACE INTO " + TABLE_INJECTIONS + " ("
				+ COL_INJECTION_ID + ", " + COL_UNITS_INTENDED + ", "
				+ COL_UNITS_DELIVERED + ", " + COL_TEMP_RATE + ", "
				+ COL_DATETIME_INTENDED + ", " + COL_DATETIME_DELIVERED + ", "
				+ COL_CUR_IOB_UNITS + ", " + COL_CUR_BG_UNITS + ", "
				+ COL_CORRECTION_UNITS + ", " + COL_CARBS_TO_COVER + ", "
				+ COL_CARBS_UNITS + ", " + COL_CUR_BASAL_UNITS + ", "
				+ COL_ALL_MEAL_CARBS_ABSORBED + ", " + COL_STATUS
				+ ") values (" + injectionId + ", " + unitsIntended + ", "
				+ unitsDelivered + ", " + tempRate + ", '"
				+ Util.convertDateToString(datetimeIntended) + "', '"
				+ Util.convertDateToString(datetimeDelivered) + "', "
				+ curIobUnits + ", " + curBgUnits + ", " + correctionUnits
				+ ", " + carbsToCover + ", " + carbsUnits + ", "
				+ curBasalUnits + ", " + allMealCarbsAbsorbed + ", '" + status
				+ "')";
	}

	public void setFromXml(String xml) {
		injectionId = new Integer(Util.getValueFromXml(xml, "injection_id"));
		unitsIntended = new Float(Util.getValueFromXml(xml, "units_intended"));
		unitsDelivered = new Float(Util.getValueFromXml(xml, "units_delivered"));
		tempRate = new Float(Util.getValueFromXml(xml, "temp_rate"));
		datetimeIntended = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_intended"));
		datetimeDelivered = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_delivered"));
		curIobUnits = new Float(Util.getValueFromXml(xml, "cur_iob_units"));
		curBgUnits = new Float(Util.getValueFromXml(xml, "cur_bg_units"));
		correctionUnits = new Float(Util.getValueFromXml(xml,
				"correction_units"));
		carbsToCover = new Integer(Util.getValueFromXml(xml, "carbs_to_cover"));
		carbsUnits = new Float(Util.getValueFromXml(xml, "carbs_units"));
		curBasalUnits = new Float(Util.getValueFromXml(xml, "cur_basal_units"));
		allMealCarbsAbsorbed = Util.getValueFromXml(xml,
				"all_meal_carbs_absorbed").equalsIgnoreCase("true") ? true
				: false;
		status = Util.getValueFromXml(xml, "status");
	}

	public String toString() {
		return "injection of " + unitsDelivered + " units was given at at "
				+ Util.convertDateToPrettyString(datetimeDelivered)
				+ " to correct from " + curBgUnits + " with " + curIobUnits
				+ " iob and eating " + carbsToCover + "g of carbs.";
	}

}
