package com.erjr.cloop.entities;

import java.util.Date;

import android.content.Context;

import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.cloop.main.Util;

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
		if (injXml == null || injXml.length() <= 0) {
			return;
		}
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
				+ curBasalUnits + ", " + (allMealCarbsAbsorbed ? 1 : 0) + ", '"
				+ status + "')";
	}

	public void setFromXml(String xml) {
		injectionId = Util.nullOrInteger(Util.getValueFromXml(xml,
				"injection_id"));
		unitsIntended = Util.nullOrFloat(Util.getValueFromXml(xml,
				"units_intended"));
		unitsDelivered = Util.nullOrFloat(Util.getValueFromXml(xml,
				"units_delivered"));
		tempRate = Util.nullOrFloat(Util.getValueFromXml(xml, "temp_rate"));
		datetimeIntended = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_intended"));
		datetimeDelivered = Util.convertStringToDate(Util.getValueFromXml(xml,
				"datetime_delivered"));
		curIobUnits = Util.nullOrFloat(Util.getValueFromXml(xml,
				"cur_iob_units"));
		curBgUnits = Util
				.nullOrFloat(Util.getValueFromXml(xml, "cur_bg_units"));
		correctionUnits = Util.nullOrFloat(Util.getValueFromXml(xml,
				"correction_units"));
		carbsToCover = Util.nullOrInteger(Util.getValueFromXml(xml,
				"carbs_to_cover"));
		carbsUnits = Util.nullOrFloat(Util.getValueFromXml(xml, "carbs_units"));
		curBasalUnits = Util.nullOrFloat(Util.getValueFromXml(xml,
				"cur_basal_units"));
		
		String allMealCarbsAbsorbedStr = Util.getValueFromXml(xml,
				"all_meal_carbs_absorbed");
		if (allMealCarbsAbsorbedStr == null
				|| allMealCarbsAbsorbedStr.equalsIgnoreCase("false")) {
			allMealCarbsAbsorbed = false;
		} else {
			allMealCarbsAbsorbed = true;
		}
		
		status = Util.getValueFromXml(xml, "status");
	}

	public String toString() {
		return "injection of " + unitsDelivered + " units was given at at "
				+ Util.convertDateToPrettyString(datetimeDelivered)
				+ " to correct from " + curBgUnits + " with " + curIobUnits
				+ " iob and eating " + carbsToCover + "g of carbs.";
	}

	/**
	 * @return the injectionId
	 */
	public Integer getInjectionId() {
		return injectionId;
	}

	/**
	 * @param injectionId
	 *            the injectionId to set
	 */
	public void setInjectionId(Integer injectionId) {
		this.injectionId = injectionId;
	}

	/**
	 * @return the unitsIntended
	 */
	public Float getUnitsIntended() {
		return unitsIntended;
	}

	/**
	 * @param unitsIntended
	 *            the unitsIntended to set
	 */
	public void setUnitsIntended(Float unitsIntended) {
		this.unitsIntended = unitsIntended;
	}

	/**
	 * @return the unitsDelivered
	 */
	public Float getUnitsDelivered() {
		return unitsDelivered;
	}

	/**
	 * @param unitsDelivered
	 *            the unitsDelivered to set
	 */
	public void setUnitsDelivered(Float unitsDelivered) {
		this.unitsDelivered = unitsDelivered;
	}

	/**
	 * @return the tempRate
	 */
	public Float getTempRate() {
		return tempRate;
	}

	/**
	 * @param tempRate
	 *            the tempRate to set
	 */
	public void setTempRate(Float tempRate) {
		this.tempRate = tempRate;
	}

	/**
	 * @return the datetimeIntended
	 */
	public Date getDatetimeIntended() {
		return datetimeIntended;
	}

	/**
	 * @param datetimeIntended
	 *            the datetimeIntended to set
	 */
	public void setDatetimeIntended(Date datetimeIntended) {
		this.datetimeIntended = datetimeIntended;
	}

	/**
	 * @return the datetimeDelivered
	 */
	public Date getDatetimeDelivered() {
		return datetimeDelivered;
	}

	/**
	 * @param datetimeDelivered
	 *            the datetimeDelivered to set
	 */
	public void setDatetimeDelivered(Date datetimeDelivered) {
		this.datetimeDelivered = datetimeDelivered;
	}

	/**
	 * @return the curIobUnits
	 */
	public Float getCurIobUnits() {
		return curIobUnits;
	}

	/**
	 * @param curIobUnits
	 *            the curIobUnits to set
	 */
	public void setCurIobUnits(Float curIobUnits) {
		this.curIobUnits = curIobUnits;
	}

	/**
	 * @return the curBgUnits
	 */
	public Float getCurBgUnits() {
		return curBgUnits;
	}

	/**
	 * @param curBgUnits
	 *            the curBgUnits to set
	 */
	public void setCurBgUnits(Float curBgUnits) {
		this.curBgUnits = curBgUnits;
	}

	/**
	 * @return the correctionUnits
	 */
	public Float getCorrectionUnits() {
		return correctionUnits;
	}

	/**
	 * @param correctionUnits
	 *            the correctionUnits to set
	 */
	public void setCorrectionUnits(Float correctionUnits) {
		this.correctionUnits = correctionUnits;
	}

	/**
	 * @return the carbsToCover
	 */
	public Integer getCarbsToCover() {
		return carbsToCover;
	}

	/**
	 * @param carbsToCover
	 *            the carbsToCover to set
	 */
	public void setCarbsToCover(Integer carbsToCover) {
		this.carbsToCover = carbsToCover;
	}

	/**
	 * @return the carbsUnits
	 */
	public Float getCarbsUnits() {
		return carbsUnits;
	}

	/**
	 * @param carbsUnits
	 *            the carbsUnits to set
	 */
	public void setCarbsUnits(Float carbsUnits) {
		this.carbsUnits = carbsUnits;
	}

	/**
	 * @return the curBasalUnits
	 */
	public Float getCurBasalUnits() {
		return curBasalUnits;
	}

	/**
	 * @param curBasalUnits
	 *            the curBasalUnits to set
	 */
	public void setCurBasalUnits(Float curBasalUnits) {
		this.curBasalUnits = curBasalUnits;
	}

	/**
	 * @return the allMealCarbsAbsorbed
	 */
	public boolean isAllMealCarbsAbsorbed() {
		return allMealCarbsAbsorbed;
	}

	/**
	 * @param i
	 *            the allMealCarbsAbsorbed to set
	 */
	public void setAllMealCarbsAbsorbed(int i) {
		this.allMealCarbsAbsorbed = i == 1 ? true : false;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
