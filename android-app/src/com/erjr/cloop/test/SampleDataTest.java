package com.erjr.cloop.test;

import java.util.Calendar;

import android.content.Context;

import com.erjr.cloop.dao.AlertDataSource;
import com.erjr.cloop.dao.AutomodeDataSource;
import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.dao.HaltDataSource;
import com.erjr.cloop.dao.IOBDataSource;
import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.cloop.dao.LogDataSource;
import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.entities.Alert;
import com.erjr.cloop.entities.IOB;
import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.entities.LogRecord;
import com.erjr.cloop.entities.SGV;
import com.erjr.diabetesi1.Util;

public class SampleDataTest extends CloopTests {

	public SampleDataTest(Context ctx) {
		super(ctx);
	}

	public boolean createSampleData() {
		clearDB();
		if (!addCourses()) {
			return false;
		}
		if (!addInjections()) {
			return false;
		}
		if (!addIobs()) {
			return false;
		}
		if (!addSgvs()) {
			return false;
		}
		if (!addAlerts()) {
			return false;
		}
		if (!addLogs()) {
			return false;
		}
		if (!addHalts()) {
			return false;
		}
		if (!addAutos()) {
			return false;
		}
		return true;
	}

	private boolean addAutos() {
		AutomodeDataSource aDS = new AutomodeDataSource(ctx);
		aDS.createAutomode("simulate");
		aDS.createAutomode("lowsOnly");
		aDS.createAutomode("fullOn");
		aDS.createAutomode("off");
		return true;
	}

	private boolean addHalts() {
		HaltDataSource hDS = new HaltDataSource(ctx);
		hDS.createHalt();
		hDS.createHalt();
		return true;
	}

	private boolean addLogs() {
		LogRecord log = new LogRecord();
		String xml = "<log><log_id>1</log_id><src_device>device</src_device><datetime_logged>"
				+ Util.convertDateToString(Util.getCurrentDateTime())
				+ "</datetime_logged><code>sync_device_pump</code><type>SUCCESS</type>"
				+ "<message>Successfully synced phone-pump sgvs at 2014-08-24 03:40:02.354879</message>"
				+ "<option1>None</option1><option2>None</option2></log>";
		log.setFromXml(xml);
		LogDataSource lDS = new LogDataSource(ctx);
		lDS.saveLogRec(log);
		return true;
	}

	private boolean addAlerts() {
		Alert a = new Alert();
		String xml = "<alert><alert_id>8</alert_id><datetime_recorded>"
				+ Util.convertDateToString(Util.getCurrentDateTime())
				+ "</datetime_recorded><datetime_to_alert>"
				+ Util.convertDateToString(Util.getCurrentDateTime())
				+ "</datetime_to_alert><src>device</src><code>process_injection</code><type>info</type>"
				+ "<title>Injected 2.0u</title>"
				+ "<message>Injection #12 of 2.0 units was given at 2014-08-24 19:35:09.113529</message>"
				+ "<value>None</value><option1>None</option1><option2>None</option2></alert>";
		a.setFromXml(xml);
		AlertDataSource aDS = new AlertDataSource(ctx);
		aDS.saveAlert(a);
		return true;
	}

	private boolean addSgvs() {
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getCurrentDateTime());
		c.add(Calendar.MINUTE, -5);
		String testSgv1 = "<sgv_record><sgv_id>1</sgv_id><device_id>584923</device_id><datetime_recorded>"
				+ Util.convertDateToString(c.getTime())
				+ "</datetime_recorded><sgv>150</sgv></sgv_record>";
		c.add(Calendar.MINUTE, -5);
		String testSgv2 = "<sgv_record><sgv_id>2</sgv_id><device_id>584923</device_id><datetime_recorded>"
				+ Util.convertDateToString(c.getTime())
				+ "</datetime_recorded><sgv>140</sgv></sgv_record>";
		c.add(Calendar.MINUTE, -5);
		String testSgv3 = "<sgv_record><sgv_id>3</sgv_id><device_id>584923</device_id><datetime_recorded>"
				+ Util.convertDateToString(c.getTime())
				+ "</datetime_recorded><sgv>130</sgv></sgv_record>";
		String[] testXmlSgvs = { testSgv1, testSgv2, testSgv3 };
		SGVDataSource sDS = new SGVDataSource(ctx);
		for (String xml : testXmlSgvs) {
			SGV s = new SGV();
			s.setFromXml(xml);
			sDS.saveSGV(s);
		}
		return true;
	}

	private boolean addIobs() {
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getCurrentDateTime());
		c.add(Calendar.MINUTE, -5);
		String testIob1 = "<iob_record><datetime_iob>"
				+ Util.convertDateToString(c.getTime())
				+ "</datetime_iob><iob>2.0</iob></iob_record>";
		c.add(Calendar.MINUTE, -5);
		String testIob2 = "<iob_record><datetime_iob>"
				+ Util.convertDateToString(c.getTime())
				+ "</datetime_iob><iob>1.8</iob></iob_record>";
		c.add(Calendar.MINUTE, -5);
		String testIob3 = "<iob_record><datetime_iob>"
				+ Util.convertDateToString(c.getTime())
				+ "</datetime_iob><iob>1.5</iob></iob_record>";
		String[] testXmlIobs = { testIob1, testIob2, testIob3 };

		IOBDataSource iDS = new IOBDataSource(ctx);
		for (String xml : testXmlIobs) {
			IOB i = new IOB();
			i.setFromXml(xml);
			iDS.saveIOB(i);
		}
		return true;
	}

	private boolean addInjections() {
		String testInj1 = "<injection><injection_id>12</injection_id><units_intended>2.0</units_intended>"
				+ "<units_delivered>2.0</units_delivered><temp_rate>None</temp_rate><datetime_intended>"
				+ Util.convertDateToString(Util.getCurrentDateTime())
				+ "</datetime_intended><datetime_delivered>"
				+ Util.convertDateToString(Util.getCurrentDateTime())
				+ "</datetime_delivered><cur_iob_units>0.0</cur_iob_units><cur_bg_units>2.0</cur_bg_units>"
				+ "<correction_units>2.0</correction_units><carbs_to_cover>0</carbs_to_cover><carbs_units>0.0</carbs_units>"
				+ "<cur_basal_units>0.55</cur_basal_units><all_meal_carbs_absorbed>True</all_meal_carbs_absorbed>"
				+ "<status>successful</status></injection>";
		InjectionDataSource iDS = new InjectionDataSource(ctx);
		Injection i = new Injection();
		i.setFromXml(testInj1);
		iDS.saveInjection(i);
		return true;
	}

	private boolean addCourses() {
		CoursesDataSource cDS = new CoursesDataSource(ctx);
		cDS.createCourse(0);
		cDS.createCourse(0, Util.getCurrentDateTime(), "some comment");
		return true;
	}

}
