package com.erjr.diabetesi1;

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

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.cloop.dao.LogDataSource;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.entities.LogRecord;

public class MainActivity extends NavDrawerActivity implements
		OnItemSelectedListener {
	private static final String TAG = "MAINACTIVITY";
	private int myNotificationId;
	public static final int NAV_POSITION = 0;
	private Spinner dataOptionsSpinner;
	private Date spinnerLogsDatetimeStart;
	private Date spinnerCoursesDatetimeStart;
	private Date spinnerInjectionsDatetimeStart;

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

		// setContentView(R.layout.fragment_main);
		// setContentView(R.layout.activity_main);

		mainNotification();
		// updateList();
		addListenerOnSpinner();

		// setNavDrawer();
	}

	private void addListenerOnSpinner() {
		dataOptionsSpinner = (Spinner) findViewById(R.id.spinner1);
		dataOptionsSpinner.setOnItemSelectedListener(this);
	}

	// private void setNavDrawer() {
	// //mPlanetTitles = getResources().getStringArray(R.array.planets_array);
	// String[] mPlanetTitles = new String[1];
	// mPlanetTitles[0] = "Test 1";
	// // mPlanetTitles[1] = "Test 2";
	// DrawerLayout mDrawerLayout = (DrawerLayout)
	// findViewById(R.id.drawer_layout);
	// ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
	//
	// // Set the adapter for the list view
	// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	// R.layout.drawer_list_items, mPlanetTitles);
	// // mDrawerList.setAdapter(adapter);
	// // Set the list's click listener
	// // mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	// }

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

	// public void deleteLastMeal(View view) {
	// (new CoursesDataSource(getBaseContext())).deleteLastCourse();
	// updateList();
	// }

	private void updateList(Object[] items) {
		ListView list = (ListView) findViewById(R.id.listCourses);

		// defining Adapter for List content
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		// CoursesDataSource coursesDS = new
		// CoursesDataSource(getBaseContext());
		// List<Course> courses = coursesDS.getAllCourses();
		// for (int i = 0; i < courses.size(); i++) {
		// adapter.add(courses.get(i).toString());
		// }
		// LogDataSource logDS = new LogDataSource(getBaseContext());
		// List<LogRecord> logs = logDS.getTodaysLogs();
		// for (LogRecord log : logs) {
		// adapter.add(log.toString());
		// }
		for (Object item : items) {
			adapter.add(item.toString());
		}
		list.setAdapter(adapter);
	}

	private void updateList(List<Object> items) {
		Object[] arr = new Object[items.size()];
		items.toArray(arr);
		updateList(arr);
	}

	// private void runManageBGLService() {
	// Intent intent = new Intent(MainActivity.this,
	// ManageBGLSyncService.class);
	// PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,
	// 0, intent, 0);
	//
	// // We want the alarm to go off 5 seconds from now.
	// long firstTime = SystemClock.elapsedRealtime();
	// firstTime += 5*1000;
	//
	// // Schedule the alarm! (every 1 minute)
	// long freq = 1 * 60 * 1000;
	// AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	// am.setRepeating(AlarmManager.RTC,
	// firstTime, freq, sender);
	// }

	public void showCGMGraphActivity(View view) {
		Intent intent = new Intent(this, GraphActivity.class);
		startActivity(intent);
	}

	public void showNightActivity(View view) {
		Intent intent = new Intent(this, NightActivity.class);
		startActivity(intent);
	}

	public void showAddMealActivity(View view) {
		Intent intent = new Intent(this, AddCourseActivity.class);
		startActivity(intent);
	}

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
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		listActions("next", s.getSelectedItemPosition());
	}

	public void buttonPrev(View view) {
		Spinner s = (Spinner) findViewById(R.id.spinner1);
		listActions("prev", s.getSelectedItemPosition());
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		listActions("next", position);
	}

	private boolean listActions(String prevOrNext, int position) {
		prevOrNext = prevOrNext == null ? "next" : prevOrNext;
		switch (position) {
		case 0:
			return (prevOrNext.equals("next")) ? nextLogs() : prevLogs();
		case 1:
			return (prevOrNext.equals("next")) ? nextCourses() : prevCourses();
		case 2:
			return (prevOrNext.equals("next")) ? nextInjections() : prevInjections();
		case 3:
			Util.toast(getBaseContext(), "Alerts coming soon");
			break;
		case 4:
			Util.toast(getBaseContext(), "Automode coming soon");
			break;
		case 5:
			Util.toast(getBaseContext(), "Shutdown coming soon");
			break;
		}
		return false;
	}

	private boolean prevLogs() {
		Calendar c = Calendar.getInstance();
		// if not yet set start with this hour
		if (spinnerLogsDatetimeStart == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			spinnerLogsDatetimeStart = c.getTime();
		} else {
			// otherwise progress by one hour
			c.setTime(spinnerLogsDatetimeStart);
			c.add(Calendar.HOUR, -1);
			spinnerLogsDatetimeStart = c.getTime();
		}
		c.setTime(spinnerLogsDatetimeStart);
		c.add(Calendar.HOUR, 1);
		Date datetimeEnd = c.getTime();
		return updateLogsFromDates(spinnerLogsDatetimeStart, datetimeEnd);
	}

	private boolean nextLogs() {
		Calendar c = Calendar.getInstance();
		// if not yet set start with this hour
		if (spinnerLogsDatetimeStart == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			spinnerLogsDatetimeStart = c.getTime();
		} else {
			// otherwise progress by one hour
			c.setTime(spinnerLogsDatetimeStart);
			c.add(Calendar.HOUR, 1);
			// do not progress if looking at the current hour
			// don't update the start time if it will be after now
			if (c.getTime().before(Util.getCurrentDateTime())) {
				spinnerLogsDatetimeStart = c.getTime();
			} else {
				Util.toast(getBaseContext(), "Updating...");
			}
		}
		c.setTime(spinnerLogsDatetimeStart);
		c.add(Calendar.HOUR, 1);
		Date datetimeEnd = c.getTime();
		return updateLogsFromDates(spinnerLogsDatetimeStart, datetimeEnd);
	}

	private boolean updateLogsFromDates(Date start, Date end) {
		LogDataSource logDS = new LogDataSource(getBaseContext());
		List<LogRecord> logs = logDS.getLogsByDateRange(start, end);
		if (logs == null) {
			Util.toast(
					getBaseContext(),
					"No logs to show for "
							+ Util.convertDateToPrettyString(start));
			return false;
		}
		// update
		Util.toast(getBaseContext(),
				"Logs for " + Util.convertDateToPrettyString(start));
		updateList((List<Object>) (List<?>) logs);
		return true;
	}

	private boolean prevCourses() {
		Calendar c = Calendar.getInstance();
		// if not yet set start with today
		if (spinnerCoursesDatetimeStart == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			spinnerCoursesDatetimeStart = c.getTime();
		} else {
			// otherwise progress by one day
			c.setTime(spinnerCoursesDatetimeStart);
			c.add(Calendar.DATE, -1);
			spinnerCoursesDatetimeStart = c.getTime();
		}
		c.setTime(spinnerCoursesDatetimeStart);
		c.add(Calendar.DATE, 1);
		Date datetimeEnd = c.getTime();
		return updateCoursesFromDates(spinnerCoursesDatetimeStart, datetimeEnd);
	}

	private boolean nextCourses() {
		Calendar c = Calendar.getInstance();
		// if not yet set start with this hour
		if (spinnerCoursesDatetimeStart == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			spinnerCoursesDatetimeStart = c.getTime();
		} else {
			// otherwise progress by one hour
			c.setTime(spinnerCoursesDatetimeStart);
			c.add(Calendar.DATE, 1);
			// do not progress if looking at the current hour
			// don't update the start time if it will be after now
			if (c.getTime().before(Util.getCurrentDateTime())) {
				spinnerCoursesDatetimeStart = c.getTime();
			} else {
				Util.toast(getBaseContext(), "Updating...");
			}
		}
		c.setTime(spinnerCoursesDatetimeStart);
		c.add(Calendar.DATE, 1);
		Date datetimeEnd = c.getTime();
		return updateCoursesFromDates(spinnerCoursesDatetimeStart, datetimeEnd);
	}

	private boolean updateCoursesFromDates(Date start, Date end) {
		CoursesDataSource coursesDS = new CoursesDataSource(getBaseContext());
		List<Course> courses = coursesDS.getCoursesByDateRange(start, end);
		if (courses == null) {
			Util.toast(
					getBaseContext(),
					"No courses to show for "
							+ Util.convertDateToPrettyString(start));
			return false;
		}
		// update
		Util.toast(getBaseContext(),
				"Courses for " + Util.convertDateToPrettyString(start));
		updateList((List<Object>) (List<?>) courses);
		return true;
	}
	
	private boolean prevInjections() {
		Calendar c = Calendar.getInstance();
		// if not yet set start with today
		if (spinnerInjectionsDatetimeStart == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			spinnerInjectionsDatetimeStart = c.getTime();
		} else {
			// otherwise progress by one day
			c.setTime(spinnerInjectionsDatetimeStart);
			c.add(Calendar.DATE, -1);
			spinnerInjectionsDatetimeStart = c.getTime();
		}
		c.setTime(spinnerInjectionsDatetimeStart);
		c.add(Calendar.DATE, 1);
		Date datetimeEnd = c.getTime();
		return updateInjectionsFromDates(spinnerInjectionsDatetimeStart, datetimeEnd);
	}

	private boolean nextInjections() {
		Calendar c = Calendar.getInstance();
		// if not yet set start with this hour
		if (spinnerInjectionsDatetimeStart == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			spinnerInjectionsDatetimeStart = c.getTime();
		} else {
			// otherwise progress by one hour
			c.setTime(spinnerInjectionsDatetimeStart);
			c.add(Calendar.DATE, 1);
			// do not progress if looking at the current hour
			// don't update the start time if it will be after now
			if (c.getTime().before(Util.getCurrentDateTime())) {
				spinnerInjectionsDatetimeStart = c.getTime();
			} else {
				Util.toast(getBaseContext(), "Updating...");
			}
		}
		c.setTime(spinnerInjectionsDatetimeStart);
		c.add(Calendar.DATE, 1);
		Date datetimeEnd = c.getTime();
		return updateInjectionsFromDates(spinnerInjectionsDatetimeStart, datetimeEnd);
	}

	private boolean updateInjectionsFromDates(Date start, Date end) {
		InjectionDataSource InjectionsDS = new InjectionDataSource(getBaseContext());
		List<Injection> Injections = InjectionsDS.getInjectionsByDateRange(start, end);
		if (Injections == null) {
			Util.toast(
					getBaseContext(),
					"No Injections to show for "
							+ Util.convertDateToPrettyString(start));
			return false;
		}
		// update
		Util.toast(getBaseContext(),
				"Injections for " + Util.convertDateToPrettyString(start));
		updateList((List<Object>) (List<?>) Injections);
		return true;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}