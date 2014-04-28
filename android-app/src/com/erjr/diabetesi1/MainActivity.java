package com.erjr.diabetesi1;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.entities.Course;

public class MainActivity extends ListActivity {
	private static final String TAG = "MAINACTIVITY";
	private CoursesDataSource datasource;
	private int myNotificationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		datasource = new CoursesDataSource(this);
		datasource.open();

		List<Course> courses = datasource.getAllCourses();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Course> adapter = new ArrayAdapter<Course>(this,
				android.R.layout.simple_list_item_1, courses);
		setListAdapter(adapter);

		NumberPicker carbs100sPicker = (NumberPicker) findViewById(R.id.numberPicker100s);
		NumberPicker carbs10sPicker = (NumberPicker) findViewById(R.id.numberPicker10s);
		NumberPicker carbs1sPicker = (NumberPicker) findViewById(R.id.numberPicker1s);
		// disable keyboard
		carbs100sPicker
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		carbs10sPicker
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		carbs1sPicker
				.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		// set min and max values
		carbs100sPicker.setMaxValue(9);
		carbs10sPicker.setMaxValue(9);
		carbs1sPicker.setMaxValue(9);
		carbs100sPicker.setMinValue(0);
		carbs10sPicker.setMinValue(0);
		carbs1sPicker.setMinValue(0);

		mainNotification();
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

		// test 2
		Intent myIntent = new Intent(MainActivity.this,
				PersistentNotificationService.class);
		myIntent.putExtra("myNotificationId", myNotificationId);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				MainActivity.this, 0, myIntent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int reoccurance = 1 * 1 * 1000;
		long trigger = System.currentTimeMillis() + (10 * 1000); 
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				trigger, reoccurance, pendingIntent);

	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Course> adapter = (ArrayAdapter<Course>) getListAdapter();
		Course course = null;
		switch (view.getId()) {
		case R.id.add:
			// save the new comment to the database
			int carbs100s = ((NumberPicker) findViewById(R.id.numberPicker100s))
					.getValue();
			int carbs10s = ((NumberPicker) findViewById(R.id.numberPicker10s))
					.getValue();
			int carbs1s = ((NumberPicker) findViewById(R.id.numberPicker1s))
					.getValue();
			int carbs = carbs100s * 100 + carbs10s * 10 + carbs1s;
			course = datasource.createCourse(carbs);
			adapter.add(course);
			break;
		case R.id.delete:
			if (getListAdapter().getCount() > 0) {
				course = (Course) getListAdapter().getItem(0);
				datasource.deleteComment(course);
				adapter.remove(course);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	public void buttonSync(View view) {
		// BTSync btSync = new BTSync();
		// List<Course> courses = datasource.getCoursesToTransfer();
		// String transStr = "<courses>";
		// for(Course course : courses) {
		// transStr += course.toXML();
		// }
		// transStr += "</courses>";
		// btSync.write(transStr);
		// String str = btSync.read();
		// if(str.contains("transfer successful")) {
		// for(Course c: courses) {
		// c.setTransfered("yes");
		// datasource.saveCourse(c);
		// }
		// }
		// Log.i(TAG, str);
	 BTSyncServer btServer = new BTSyncServer(getBaseContext());
		 btServer.start();
		
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}