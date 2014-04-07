package com.erjr.diabetesi1;

import java.util.List;

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.entities.Course;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;

public class MainActivity extends ListActivity {
	private static final String TAG = "MAINACTIVITY";
	private CoursesDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		datasource = new CoursesDataSource(this);
		datasource.open();

		List<Course> portions = datasource.getAllComments();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Course> adapter = new ArrayAdapter<Course>(this,
				android.R.layout.simple_list_item_1, portions);
		setListAdapter(adapter);
		NumberPicker carbs100sPicker = (NumberPicker) findViewById(R.id.numberPicker100s);
		NumberPicker carbs10sPicker = (NumberPicker) findViewById(R.id.numberPicker10s);
		NumberPicker carbs1sPicker = (NumberPicker) findViewById(R.id.numberPicker1s);
		carbs100sPicker.setMaxValue(10);
		carbs10sPicker.setMaxValue(10);
		carbs1sPicker.setMaxValue(10);
		carbs100sPicker.setMinValue(0);
		carbs10sPicker.setMinValue(0);
		carbs1sPicker.setMinValue(0);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Course> adapter = (ArrayAdapter<Course>) getListAdapter();
		Course portion = null;
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
			portion = datasource.createCourse(carbs);
			adapter.add(portion);
			break;
		case R.id.delete:
			if (getListAdapter().getCount() > 0) {
				portion = (Course) getListAdapter().getItem(0);
				datasource.deleteComment(portion);
				adapter.remove(portion);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	public void buttonSync(View view) {
		BTSync btSync = new BTSync();
		List<Course> portions = datasource.getAllComments();
		String transStr = "";
		for(Course portion : portions) {
			transStr += portion.toXML();
		}
		btSync.write(transStr);
		String str = btSync.read();
		Log.i(TAG, str);
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