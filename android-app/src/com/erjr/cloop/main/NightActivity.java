package com.erjr.cloop.main;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.entities.SGV;
import com.erjr.diabetesi1.R;

public class NightActivity extends Activity {

	private static final Integer highBgLimit = 175;
	private static final Integer lowBgLimit = 90;
	private static final Integer extremeLowBgLimit = 70;
	private static final Integer extremeHighBgLimit = 250;
	private static Date snoozeAlarmUntil = null;
	protected PowerManager.WakeLock mWakeLock;
	private String TAG = "FullScreenBG";
	private boolean isActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_bg);
		setContentView(R.layout.fragment_full_screen_bg);
		isActive = true;
		// TODO: Not sure what this is used for. should find out.
//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}

		/*
		 * This code together with the one in onDestroy() will make the screen
		 * be always on until this Activity gets destroyed.
		 */
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
		this.mWakeLock.acquire();

		// update every few secs
		// from http://steve.odyfamily.com/?p=12
		Timer myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 10000, 5000);

	}

	private void TimerMethod() {
		// This method is called directly by the timer
		// and runs in the same thread as the timer.

		// We call the method that will work with the UI
		// through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {

			// This method runs in the same thread as the UI.
			updateNumbers();
			// Do something to the UI thread here

		}
	};

	private void updateNumbers() {
		if(!isActive) {
			return;
		}
		SGVDataSource SGVDS = new SGVDataSource(getBaseContext());
		SGV[] recentSGVs = SGVDS.getRecentSGVs(1);
		Date currentDate = Util.getCurrentDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.HOUR, -1);

		if (recentSGVs == null
				|| recentSGVs[recentSGVs.length - 1].getDatetimeRecorded()
						.before(c.getTime())) {
			// if stale data then clear numbers
			updateNumber(null, R.id.BGMostRecent);
		} else {
			SGV mostRecentSGV = recentSGVs[recentSGVs.length - 1];
			// if semi current then update
			updateNumber(mostRecentSGV, R.id.BGMostRecent);
		}
	}

	private void updateNumber(SGV sgv, int idToUpdate) {
		if (sgv == null) {
			((TextView) findViewById(idToUpdate)).setText("NO DATA");
			((TextView) findViewById(idToUpdate)).setTextColor(Color.WHITE);
			return;
		}
		TextView text = ((TextView) findViewById(idToUpdate));
		text.setText(sgv.getSg() + " - "
				+ Util.convertDateToPrettyString(sgv.getDatetimeRecorded()));
		if (sgv.getSg() > highBgLimit) {
			text.setTextColor(Color.BLUE);
		} else if (sgv.getSg() < lowBgLimit) {
			text.setTextColor(Color.RED);
		} else {
			text.setTextColor(Color.GREEN);
			snoozeAlarmUntil = null;
		}

		Date currentDate = Util.getCurrentDateTime();
		// if extremely low or high then play alert regardless
		if (sgv.getSg() < extremeLowBgLimit || sgv.getSg() > extremeHighBgLimit) {
			playAlertSound(true);
		} else if ((sgv.getSg() < lowBgLimit || sgv.getSg() > highBgLimit)
				&& (snoozeAlarmUntil == null || currentDate
						.after(snoozeAlarmUntil))) {
			playAlertSound(false);
		}

	}

	private void playAlertSound(boolean isExtreme) {
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(),
				R.raw.alarm);
		for (int i = 0; i < 4; i++) {
			mp.start();
		}
	}

	public void snoozeAlarm(View view) {
		Date currentDate = Util.getCurrentDateTime();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		SGVDataSource SGVDS = new SGVDataSource(getBaseContext());
		SGV[] recentSGVs = SGVDS.getRecentSGVs(1);
		SGV mostRecentSGV = recentSGVs[recentSGVs.length - 1];
		if (mostRecentSGV == null) {
			c.add(Calendar.MINUTE, 30);
			snoozeAlarmUntil = c.getTime();
		} else if (mostRecentSGV.getSg() < lowBgLimit) {
			c.add(Calendar.MINUTE, 30);
			snoozeAlarmUntil = c.getTime();
		} else if (mostRecentSGV.getSg() > highBgLimit) {
			c.add(Calendar.MINUTE, 90);
			snoozeAlarmUntil = c.getTime();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_screen_bg, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_full_screen_bg,
					container, false);
			return rootView;
		}
	}
	
	@Override
	public void onStop() {
		isActive = false;
	}

	@Override
	public void onDestroy() {
		isActive = false;
		this.mWakeLock.release();
		super.onDestroy();
	}
}