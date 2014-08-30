package com.erjr.cloop.main;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.entities.Course;
import com.erjr.main.R;

public class AddCourseActivity extends Activity {

	private CoursesDataSource coursesDS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_meal);
		setContentView(R.layout.fragment_enter_meal);

		// if (savedInstanceState == null) {
		// getFragmentManager().beginTransaction()
		// .add(R.id.container, new PlaceholderFragment()).commit();
		// }

		coursesDS = new CoursesDataSource(this);
		coursesDS.open();

		List<Course> courses = coursesDS.getAllCourses();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		// ArrayAdapter<Course> adapter = new ArrayAdapter<Course>(this,
		// , courses);
		// setListAdapter(adapter);

		defaultNumPickers();
	}

	private void defaultNumPickers() {
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

	}

	public void goBack(View view) {
		finish();
	}

	public void goToConfirm(View view) {
		// save the new comment to the database
		int carbs = getCarbsSelected();
		Date timeToComsume = getTimeToConsumeSelected();

		// confirmation alert
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Confirm Meal")
				.setMessage(
						"Are you sure you want to add " + carbs
								+ " carbs to be consumed at "
								+ Util.convertDateToPrettyString(timeToComsume))
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// if yes add meal
								addMeal();
								finish();
							}
						}).setNegativeButton("No", null).show();
	}

	private void addMeal() {
		int carbs = getCarbsSelected();
		Date timeToConsume = getTimeToConsumeSelected();
		String comment = getCommentEntered();
		Course course = coursesDS.createCourse(carbs, timeToConsume, comment);
		Util.toast(getBaseContext(), "Successfully added course #"+course.getCourseID()+" of "+course.getCarbs()+" carbs.");
	}

	private String getCommentEntered() {
		EditText commentField = (EditText) findViewById(R.id.commentField);
		return commentField.getText().toString();
	}

	private int getCarbsSelected() {
		int carbs100s = ((NumberPicker) findViewById(R.id.numberPicker100s))
				.getValue();
		int carbs10s = ((NumberPicker) findViewById(R.id.numberPicker10s))
				.getValue();
		int carbs1s = ((NumberPicker) findViewById(R.id.numberPicker1s))
				.getValue();
		int carbs = carbs100s * 100 + carbs10s * 10 + carbs1s;
		return carbs;
	}

	private Date getTimeToConsumeSelected() {
		TimePicker timeToConsumeField = (TimePicker) findViewById(R.id.timePicker);
		Integer hour = timeToConsumeField.getCurrentHour();
		Integer minute = timeToConsumeField.getCurrentMinute();
		Date now = Util.getCurrentDateTime();
		now.setHours(hour);
		now.setMinutes(minute);
		return now;
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	// public void updateList() {
	// @SuppressWarnings("unchecked")
	// ArrayAdapter<Course> adapter = (ArrayAdapter<Course>) getListAdapter();
	// adapter.notifyDataSetChanged();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_meal, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_enter_meal,
					container, false);
			return rootView;
		}
	}

}
