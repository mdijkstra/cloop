package com.erjr.cloop.test;

import android.content.Context;

import com.erjr.cloop.dao.AutomodeDataSource;
import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.dao.HaltDataSource;
import com.erjr.cloop.entities.Automode;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Halt;
import com.erjr.cloop.main.BTSyncThread;
import com.erjr.cloop.main.Util;

public class BTSyncTest extends CloopTests {

	// import strings
	String testSgv1 = "<sgv_record><sgv_id>508</sgv_id><device_id>584923</device_id><datetime_recorded>2014-08-24T16:50:00</datetime_recorded><sgv>150</sgv></sgv_record>";
	String testSgv2 = "<sgv_record><sgv_id>509</sgv_id><device_id>584923</device_id><datetime_recorded>2014-08-24T16:45:00</datetime_recorded><sgv>146</sgv></sgv_record>";
	String testSgv3 = "<sgv_record><sgv_id>517</sgv_id><device_id>584923</device_id><datetime_recorded>2014-08-24T16:05:00</datetime_recorded><sgv>120</sgv></sgv_record>";
	String[] testXmlSgvs = { "", testSgv1, testSgv2, testSgv3,
			testSgv1 + testSgv2, testSgv1 + testSgv2 + testSgv3 };

	String testIob1 = "<iob_record><datetime_iob>2014-08-24T19:35:00</datetime_iob><iob>2.0</iob><iob_bg>180</iob_bg></iob_record>";
	String testIob2 = "<iob_record><datetime_iob>2014-08-24T19:40:00</datetime_iob><iob>1.5</iob><iob_bg>165</iob_bg></iob_record>";
	String testIob3 = "<iob_record><datetime_iob>2014-08-24T19:45:00</datetime_iob><iob>1.0</iob><iob_bg>150</iob_bg></iob_record>";
	String[] testXmlIobs = { "", testIob1, testIob2, testIob3,
			testIob1 + testIob2, testIob1 + testIob2 + testIob3 };

	String testInj1 = "<injection><injection_id>12</injection_id><units_intended>2.0</units_intended><units_delivered>2.0</units_delivered><temp_rate>None</temp_rate><datetime_intended>2014-08-24T19:35:09</datetime_intended><datetime_delivered>2014-08-24T19:35:38</datetime_delivered><cur_iob_units>0.0</cur_iob_units><cur_bg_units>2.0</cur_bg_units><correction_units>2.0</correction_units><carbs_to_cover>0</carbs_to_cover><carbs_units>0.0</carbs_units><cur_basal_units>0.55</cur_basal_units><all_meal_carbs_absorbed>True</all_meal_carbs_absorbed><status>successful</status></injection>";
	String[] testXmlInjs = { "", testInj1 };

	String testLog1 = "<log><log_id>244</log_id><src_device>device</src_device><datetime_logged>2014-08-24T03:40:37</datetime_logged><code>sync_device_pump</code><type>SUCCESS</type><message>Successfully synced phone-pump sgvs at 2014-08-24 03:40:02.354879</message><option1>None</option1><option2>None</option2></log>";
	String testLog2 = "<log><log_id>245</log_id><src_device>device</src_device><datetime_logged>2014-08-24T03:41:24</datetime_logged><code>sync_device_phone</code><type>SUCCESS</type><message>Successfully completed phone-device sync at 2014-08-24 03:40:44.084767</message><option1>None</option1><option2>None</option2></log>";
	String testLog3 = "<log><log_id>246</log_id><src_device>device</src_device><datetime_logged>2014-08-24T03:42:59</datetime_logged><code>sync_device_phone</code><type>SUCCESS</type><message>Successfully completed phone-device sync at 2014-08-24 03:42:18.975106</message><option1>None</option1><option2>None</option2></log>";
	String[] testXmlLogs = { "", testLog1, testLog2, testLog3,
			testLog1 + testLog2, testLog1 + testLog2 + testLog3 };

	String testAlert1 = "<alert><alert_id>8</alert_id><datetime_recorded>2014-08-24T19:35:40</datetime_recorded><datetime_to_alert>2014-08-24T19:35:09</datetime_to_alert><src>device</src><code>process_injection</code><type>info</type><title>Injected 2.0u</title><message>Injection #12 of 2.0 units was given at 2014-08-24 19:35:09.113529</message><value>None</value><option1>None</option1><option2>None</option2></alert>";
	String[] testXmlAlerts = { "", testAlert1 };
	private Course exportCourse1;
	private Course exportCourse2;
	private Halt exportHalt1;
	private Halt exportHalt2;
	private Automode exportAuto1;
	private Automode exportAuto2;
	private Automode exportAuto3;
	private Automode exportAuto4;

	public BTSyncTest(Context ctx) {
		super(ctx);
	}

	public boolean runTests() {
		if (!testImports()) {
			return false;
		}
		if (!testExports()) {
			return false;
		}
		clearDB();
		return true;
	}

	/************************* Testing Import functionality *************************/

	public boolean testImports() {
		if (!testBasics()) {
			return false;
		}
		if (!testMixedXmls()) {
			return false;
		}
		if (!testFullXml()) {
			return false;
		}
		return true;
	}

	private boolean testFullXml() {
		if (!testImport(getFullXml(), true)) {
			Util.toast(ctx, "One of the full imports didn't work :(");
			return false;
		}
		if (!testImport(getFullXml(), false)) {
			Util.toast(ctx, "One of the full imports didn't work :(");
			return false;
		}
		if (!testImport(getFullXml(), true)) {
			Util.toast(ctx, "One of the full imports didn't work :(");
			return false;
		}
		return true;
	}

	private String getFullXml() {
		String xml = "<sgvs>" + testXmlSgvs[testXmlSgvs.length - 1] + "</sgvs>";
		xml += "<iobs>" + testXmlIobs[testXmlIobs.length - 1] + "</iobs>";
		xml += "<injections>" + testXmlInjs[testXmlInjs.length - 1]
				+ "</injections>";
		xml += "<logs>" + testXmlLogs[testXmlLogs.length - 1] + "</logs>";
		xml += "<alerts>" + testXmlAlerts[testXmlAlerts.length - 1]
				+ "</alerts>";
		return xml + "</EOM>";
	}

	private boolean testMixedXmls() {
		for (String xmlSgv : testXmlSgvs) {
			for (String xmlInj : testXmlInjs) {
				for (String xmlLog : testXmlLogs) {
					for (String xmlAlert : testXmlAlerts) {
						for (String xmlIob : testXmlIobs) {
							String xml = "<sgvs>" + xmlSgv + "</sgvs>";
							xml += "<iobs>" + xmlIob + "</iobs>";
							xml += "<injections>" + xmlInj + "</injections>";
							xml += "<logs>" + xmlLog + "</logs>";
							xml += "<alerts>" + xmlAlert + "</alerts>";
							xml += "</EOM>";
							if (!testImport(xml, true)) {
								Util.toast(ctx,
										"One of the import mixes didn't work");
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	private boolean testBasics() {
		if (!testImport(null, true)) {
			Util.toast(ctx, "Couldn't import null string");
			return false;
		}
		if (!testImport("", true)) {
			Util.toast(ctx, "Couldn't import empty string");
			return false;
		}
		if (!testImport("</EOM>", true)) {
			Util.toast(ctx, "Couldn't import eom string");
			return false;
		}
		if (!testImport(
				"<sgvs></sgvs><injections></injections><logs></logs><alerts></alerts><iobs></iobs></EOM>",
				true)) {
			Util.toast(ctx, "Couldn't import empty sets");
			return false;
		}
		return true;
	}

	private boolean testImport(String xml, boolean clearDb) {
		try {
			BTSyncThread btSync = new BTSyncThread(ctx);
			if (clearDb)
				clearDB();
			btSync.processDataReceived(xml);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/************************* Testing Export functionality *************************/

	private boolean testExports() {
		BTSyncThread btSync = new BTSyncThread(ctx);
		clearDB();
		if (!addCourses()) {
			return false;
		}
		String exportXml = btSync.getDataToSend();
		if (!testExportWCourses(exportXml)) {
			return false;
		}
		if (!addHalts()) {
			return false;
		}
		exportXml = btSync.getDataToSend();
		if (!testExportWHalts(exportXml) || !testExportWCourses(exportXml)) {
			return false;
		}
		if (!addAutos()) {
			return false;
		}
		exportXml = btSync.getDataToSend();
		if (!testExportWAuto(exportXml) || !testExportWHalts(exportXml)
				|| !testExportWCourses(exportXml)) {
			return false;
		}
		clearDB();
		return true;
	}

	private boolean testExportWAuto(String exportXml) {
		if (exportXml == null) {
			return false;
		}
		if (!exportContainsAuto(exportAuto1, exportXml)) {
			return false;
		}
		if (!exportContainsAuto(exportAuto2, exportXml)) {
			return false;
		}
		if (!exportContainsAuto(exportAuto3, exportXml)) {
			return false;
		}
		if (!exportContainsAuto(exportAuto4, exportXml)) {
			return false;
		}
		return true;
	}

	private boolean exportContainsAuto(Automode exportAuto, String exportXml) {
		String autoXml = "<automode><is_on>" + exportAuto.getIsOn()
				+ "</is_on><datetime_recorded>"
				+ Util.convertDateToString(exportAuto.getDatetimeRecorded())
				+ "</datetime_recorded><automode_switch_id>"
				+ exportAuto.getAutomodeSwitchId()
				+ "</automode_switch_id></automode>";
		if (!exportXml.contains(autoXml)) {
			Util.toast(ctx, "Automode " + exportAuto.getIsOn()
					+ " didn't export properly.");
			return false;
		}
		return true;
	}

	private boolean testExportWHalts(String exportXml) {
		if (exportXml == null) {
			return false;
		}
		if (!exportContainsHalt(exportHalt1, exportXml)) {
			return false;
		}
		if (!exportContainsHalt(exportHalt2, exportXml)) {
			return false;
		}
		return true;
	}

	private boolean exportContainsHalt(Halt exportHalt, String exportXml) {
		String haltXml = "<halt><datetime_issued>"
				+ Util.convertDateToString(exportHalt.getDatetimeIssued())
				+ "</datetime_issued><halt_id>" + exportHalt.getHaltID()
				+ "</halt_id></halt>";
		if (!exportXml.contains(haltXml)) {
			Util.toast(ctx, "Halt " + exportHalt.getHaltID()
					+ " didn't export properly");
			return false;
		}
		return true;
	}

	private boolean testExportWCourses(String exportXml) {
		if (exportXml == null) {
			return false;
		}
		if (!exportContainsCourse(exportCourse1, exportXml)) {
			return false;
		}
		if (!exportContainsCourse(exportCourse2, exportXml)) {
			return false;
		}
		return true;
	}

	private boolean exportContainsCourse(Course exportCourse, String exportXml) {
		String courseXml = "<course><serv_quantity>0.0</serv_quantity><food_id>0</food_id><course_id>"
				+ exportCourse.getCourseID()
				+ "</course_id><comment>"
				+ exportCourse.getComment()
				+ "</comment><carbs>"
				+ exportCourse.getCarbs()
				+ "</carbs><datetime_ideal_injection>"
				+ Util.convertDateToString(exportCourse
						.getDatetimeIdealInjection())
				+ "</datetime_ideal_injection><datetime_consumption>"
				+ Util.convertDateToString(exportCourse
						.getDatetimeConsumption())
				+ "</datetime_consumption></course>";
		if (!exportXml.contains(courseXml)) {
			Util.toast(ctx, "Course " + exportCourse.getCourseID()
					+ " didn't export properly.");
			return false;
		}
		return true;
	}

	private boolean addAutos() {
		AutomodeDataSource aDS = new AutomodeDataSource(ctx);
		exportAuto1 = aDS.createAutomode("lowsOnly");
		exportAuto2 = aDS.createAutomode("fullOn");
		exportAuto3 = aDS.createAutomode("simulate");
		exportAuto4 = aDS.createAutomode("off");
		return true;
	}

	private boolean addHalts() {
		HaltDataSource hDS = new HaltDataSource(ctx);
		exportHalt1 = hDS.createHalt();
		exportHalt2 = hDS.createHalt();
		return true;
	}

	private boolean addCourses() {
		CoursesDataSource cDS = new CoursesDataSource(ctx);
		exportCourse1 = cDS.createCourse(30, Util.getCurrentDateTime(),
				"comment 1");
		exportCourse2 = cDS.createCourse(25);
		return true;

	}
}