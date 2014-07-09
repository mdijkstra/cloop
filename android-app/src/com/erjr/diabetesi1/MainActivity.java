package com.erjr.diabetesi1;

import java.util.List;

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.entities.Course;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MAINACTIVITY";
	private int myNotificationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		// start the BTSync service (if not already running)
		// Intent intent = new Intent(this, BTSyncService.class);
		// startService(intent);

		setContentView(R.layout.fragment_main);
		mainNotification();
		updateList();
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

	public void deleteLastMeal(View view) {
		(new CoursesDataSource(getBaseContext())).deleteLastCourse();
		updateList();
	}

	private void updateList() {
		ListView list = (ListView) findViewById(R.id.listCourses);

		// defining Adapter for List content
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		CoursesDataSource coursesDS = new CoursesDataSource(getBaseContext());
		List<Course> courses = coursesDS.getAllCourses();
		for (int i = 0; i < courses.size(); i++) {
			adapter.add(courses.get(i).toString());
		}
		list.setAdapter(adapter);

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
		Intent intent = new Intent(this, CGMGraph.class);
		startActivity(intent);
	}

	public void showNightActivity(View view) {
		Intent intent = new Intent(this, FullScreenBG.class);
		startActivity(intent);
	}

	public void showAddMealActivity(View view) {
		Intent intent = new Intent(this, AddCourseActivity.class);
		startActivity(intent);
	}

	// public void testManageBGL(View view) {
	// return;
	// }

	@Override
	protected void onResume() {
		updateList();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}