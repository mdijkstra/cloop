package com.erjr.cloop.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.erjr.cloop.dao.AlertDataSource;
import com.erjr.cloop.dao.AutomodeDataSource;
import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.dao.HaltDataSource;
import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.cloop.dao.LogDataSource;
import com.erjr.cloop.entities.Alert;
import com.erjr.cloop.entities.Automode;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Halt;
import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.entities.LogRecord;
import com.erjr.diabetesi1.R;

public class MainActivity extends NavDrawerActivity implements
		OnItemSelectedListener {
	private static final String TAG = "MAINACTIVITY";
	private int myNotificationId;
	private Spinner dataOptionsSpinner;
	private Date spinnerLogsDatetimeStart;
	private Date spinnerCoursesDatetimeStart;
	private Date spinnerInjectionsDatetimeStart;
	private Date spinnerHaltsDatetimeStart;
	private Date spinnerAutosDatetimeStart;
	private Date spinnerAlertsDatetimeStart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setDrawer(R.id.navigation_drawer_fragment, R.id.main_activity,
				NavDrawerActivity.NAV_POSITION_MAIN);
		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		// StrictMode.setThreadPolicy(policy);
		// start the BTSync service (if not already running)
		// Intent intent = new Intent(this, BTSyncService.class);
		// startService(intent);
		mainNotification();
		addListenerOnSpinner();
	}

	private void addListenerOnSpinner() {
		dataOptionsSpinner = (Spinner) findViewById(R.id.spinner1);
		dataOptionsSpinner.setOnItemSelectedListener(this);
	}

	public void mainNotification() {
		// create the notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Latest BG")
				.setContentText(
						"83 + "
								+ Util.convertDateToString(Util
										.getCurrentDateTime()));
		mBuilder.setOngoing(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(myNotificationId, mBuilder.build());

		// test 3 -
		// http://www.coderzheaven.com/2011/07/31/how-to-setup-a-repeating-alarm-in-android/
		Intent intent = new Intent(MainActivity.this,
				PersistentNotification.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0,
				intent, 0);

		// We want the alarm to go off 5 seconds from now.
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += 5 * 1000;

		// Schedule the alarm! (every 1 minute)
		long freq = 1 * 60 * 1000;
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC, firstTime, freq, sender);

		// runManageBGLService();
	}

	private void updateList(Object[] items) {
		if (items == null) {
			clearList();
			return;
		}

		ListView list = (ListView) findViewById(R.id.listCourses);

		// defining Adapter for List content
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		for (Object item : items) {
			adapter.add(item.toString());
		}
		list.setAdapter(adapter);
	}

	private void clearList() {
		String[] emptyStrings = new String[1];
		emptyStrings[0] = "";
		updateList(emptyStrings);
	}

	private void updateList(List<Object> items) {
		if (items == null) {
			clearList();
			return;
		}
		Object[] arr = new Object[items.size()];
		items.toArray(arr);
		updateList(arr);
	}

	// public void showCGMGraphActivity(View view) {
	// Intent intent = new Intent(this, GraphActivity.class);
	// startActivity(intent);
	// }
	//
	// public void showNightActivity(View view) {
	// Intent intent = new Intent(this, NightActivity.class);
	// startActivity(intent);
	// }
	//
	// public void showAddMealActivity(View view) {
	// Intent intent = new Intent(this, AddCourseActivity.class);
	// startActivity(intent);
	// }

	@Override
	protected void onResume() {
		// updateList();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void buttonNext(View view) {
		clearList();
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		listActions(true, s.getSelectedItemPosition());
	}

	public void buttonPrev(View view) {
		clearList();
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		listActions(false, s.getSelectedItemPosition());
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		listActions(true, position);
	}

	private void listActions(boolean isNext, int position) {
		switch (position) {
		case 0:
			updateLogs(isNext);
			return;
		case 1:
			updateCourses(isNext);
			return;
		case 2:
			updateInjections(isNext);
			return;
		case 3:
			updateAlerts(isNext);
			return;
		case 4:
			updateAutos(isNext);
			return;
		case 5:
			updateHalts(isNext);
			return;
		}
	}

	private void updateCourses(boolean isNext) {
		Date[] dates = getDateRange(spinnerCoursesDatetimeStart, 1440, isNext);
		CoursesDataSource coursesDS = new CoursesDataSource(getBaseContext());
		List<Course> courses = coursesDS.getCoursesByDateRange(dates[0],
				dates[1]);
		toastListUpdate((List<Object>) (List<?>) courses, "Courses", dates[0]);
		updateList((List<Object>) (List<?>) courses);
	}

	private void updateLogs(boolean isNext) {
		Date[] dates = getDateRange(spinnerLogsDatetimeStart, 60, isNext);
		LogDataSource logDS = new LogDataSource(getBaseContext());
		List<LogRecord> logs = logDS.getLogsByDateRange(dates[0], dates[1]);
		toastListUpdate((List<Object>) (List<?>) logs, "Logs", dates[0]);
		updateList((List<Object>) (List<?>) logs);
	}

	private void updateInjections(boolean isNext) {
		Date[] dates = getDateRange(spinnerInjectionsDatetimeStart, 1440,
				isNext);
		InjectionDataSource InjectionsDS = new InjectionDataSource(
				getBaseContext());
		List<Injection> Injections = InjectionsDS.getInjectionsByDateRange(
				dates[0], dates[1]);
		toastListUpdate((List<Object>) (List<?>) Injections, "Injections",
				dates[0]);
		updateList((List<Object>) (List<?>) Injections);
	}

	private void updateHalts(boolean isNext) {
		Date[] dates = getDateRange(spinnerHaltsDatetimeStart, 1440, isNext);
		HaltDataSource hDS = new HaltDataSource(getBaseContext());
		List<Halt> halts = hDS.getHaltsByDateRange(dates[0], dates[1]);
		toastListUpdate((List<Object>) (List<?>) halts, "Halts", dates[0]);
		updateList((List<Object>) (List<?>) halts);
	}

	private void updateAutos(boolean isNext) {
		Date[] dates = getDateRange(spinnerAutosDatetimeStart, 1440, isNext);
		AutomodeDataSource aDS = new AutomodeDataSource(getBaseContext());
		List<Automode> autos = aDS.getAutosByDateRange(dates[0], dates[1]);
		toastListUpdate((List<Object>) (List<?>) autos, "Automodes", dates[0]);
		updateList((List<Object>) (List<?>) autos);
	}

	private void updateAlerts(boolean isNext) {
		Date[] dates = getDateRange(spinnerAlertsDatetimeStart, 360, isNext);
		AlertDataSource aDS = new AlertDataSource(getBaseContext());
		List<Alert> alerts = aDS.getAlertsByDateRange(dates[0], dates[1]);
		toastListUpdate((List<Object>) (List<?>) alerts, "Alerts", dates[0]);
		updateList((List<Object>) (List<?>) alerts);
	}

	private void toastListUpdate(List<Object> items, String listedItems,
			Date startDate) {
		if (items == null) {
			Util.toast(
					getBaseContext(),
					"No " + listedItems.toLowerCase() + " to show for "
							+ Util.convertDateToPrettyString(startDate));
		}
		// update
		Util.toast(
				getBaseContext(),
				listedItems + " for "
						+ Util.convertDateToPrettyString(startDate));
	}

	private Date[] getDateRange(Date startTime, int minutesDelta, boolean next) {
		Calendar c = Calendar.getInstance();
		if (startTime == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			if (minutesDelta >= 1440) {
				c.set(Calendar.HOUR, 0);
			}
		} else {
			c.setTime(startTime);
		}
		if (next) {
			c.add(Calendar.MINUTE, minutesDelta);
			if (c.getTime().after(Util.getCurrentDateTime())) {
				c.add(Calendar.MINUTE, -minutesDelta);
			}
		} else {
			c.add(Calendar.MINUTE, -minutesDelta);
		}
		startTime = c.getTime();
		c.add(Calendar.MINUTE, minutesDelta);
		Date endTime = c.getTime();
		Date[] arr = new Date[2];
		arr[0] = startTime;
		arr[1] = endTime;
		return arr;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}