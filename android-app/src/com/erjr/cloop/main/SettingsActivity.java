package com.erjr.cloop.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.erjr.cloop.dao.AutomodeDataSource;
import com.erjr.cloop.dao.HaltDataSource;
import com.erjr.cloop.entities.Automode;
import com.erjr.cloop.entities.Halt;
import com.erjr.cloop.test.CloopTests;
import com.erjr.cloop.test.SampleDataTest;
import com.erjr.diabetesi1.R;

public class SettingsActivity extends NavDrawerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setDrawer(R.id.navigation_drawer_fragment, R.id.settings_activity,
				NavDrawerActivity.NAV_POSITION_SETTINGS);
		updateTexts();
	}

	private void updateTexts() {
		((TextView) findViewById(R.id.currentAutomode))
				.setText(getCurAutomode());
		((TextView) findViewById(R.id.currentSwitch)).setText(getLastPower());
	}

	private String getCurAutomode() {
		AutomodeDataSource aDS = new AutomodeDataSource(getBaseContext());
		Automode a = aDS.getLatestAutomode();
		if (a == null) {
			return "No automode records";
		} else {
			return a.toString();
		}
	}

	private String getLastPower() {
		HaltDataSource hDS = new HaltDataSource(getBaseContext());
		Halt h = hDS.getLatestHalt();
		if (h == null) {
			return "No halt records";
		} else {
			return h.toString();
		}
	}

	public void automodeSwitch(View view) {
		AutomodeDataSource aDS = new AutomodeDataSource(getBaseContext());
		Automode a = aDS.getLatestAutomode();
		String isOn;
		if (a == null || a.getIsOn().equals("fullOn")) {
			isOn = "off";
		} else if (a.getIsOn().equals("off")) {
			isOn = "simulate";
		} else if (a.getIsOn().equals("simulate")) {
			isOn = "lowsOnly";
		} else {
			isOn = "fullOn";
		}
		Automode newA = aDS.createAutomode(isOn);
		Util.toast(getBaseContext(), newA.toString());
		updateTexts();
	}

	public void powerSwitch(View view) {
		HaltDataSource hDS = new HaltDataSource(getBaseContext());
		Halt h = hDS.createHalt();
		Util.toast(getBaseContext(), h.toString());
		updateTexts();
	}

	public void runAllTests(View view) {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Confirm Run Tests")
				.setMessage(
						"Are you sure you want to run the app tests? They will clear the database and this cannot be undone!")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// key component
								CloopTests test = new CloopTests(
										getBaseContext());
								boolean success = test.runAllTests();
								((TextView) findViewById(R.id.testResults))
										.setText("Test was " + success);
								updateTexts();
								// end key component
							}
						}).setNegativeButton("No", null).show();
	}

	public void addSampleData(View view) {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Confirm Add Sample Data")
				.setMessage(
						"Are you sure you want to add the sample data? This will clear the database and this cannot be undone!")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// key component
								SampleDataTest sdt = new SampleDataTest(
										getBaseContext());
								boolean success = sdt.createSampleData();
								((TextView) findViewById(R.id.sampleDataResults))
										.setText("Sample data was " + success
												+ " loaded");
								updateTexts();
								// end key component
							}
						}).setNegativeButton("No", null).show();
	}

	public void clearDB(View view) {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Confirm Clear DB")
				.setMessage(
						"Are you sure you want to clear the database? This will clear the database and this cannot be undone!")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// key component
								CloopTests test = new CloopTests(getBaseContext());
								boolean success = test.clearDB();
								((TextView) findViewById(R.id.clearDBResults))
										.setText("Database was " + success
												+ " cleared.");
								updateTexts();
								// end key component
							}
						}).setNegativeButton("No", null).show();
	}

}
