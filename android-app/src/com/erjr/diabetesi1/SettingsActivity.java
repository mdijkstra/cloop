package com.erjr.diabetesi1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.erjr.cloop.dao.AutomodeDataSource;
import com.erjr.cloop.dao.HaltDataSource;
import com.erjr.cloop.entities.Automode;
import com.erjr.cloop.entities.Halt;

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
		if (a == null || a.getIsOn().equals("yes")) {
			isOn = "no";
		} else {
			isOn = "yes";
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
}
