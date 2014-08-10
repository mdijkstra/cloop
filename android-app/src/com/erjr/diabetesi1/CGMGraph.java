package com.erjr.diabetesi1;

import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.entities.SGV;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class CGMGraph extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cgmgraph);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// get SGV data and add to series
		SGVDataSource SGVDS = new SGVDataSource(getBaseContext());
		SGV[] sgvs = SGVDS.getRecentSGVs(24);
		GraphViewData[] cgmTodayData = new GraphViewData[sgvs.length];
		for (int i = 0; i < sgvs.length; i++) {
			cgmTodayData[i] = new GraphViewData(sgvs[i].getDatetimeRecorded()
					.getTime(), sgvs[i].getSg());
		}
		GraphViewSeries cgmTodaySeries = new GraphViewSeries(cgmTodayData);

		final java.text.DateFormat dateTimeFormatter = DateFormat
				.getTimeFormat(getBaseContext());
		LineGraphView graphView = new LineGraphView(this, "Today's CGM Data") {
			@Override
			protected String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					// transform number to time for x-axis display
					return Util
							.convertDateToPrettyString(new Date((long) value));
				} else {
					return super.formatLabel(value, isValueX);
				}
			}
		};

		// add lines
		graphView.addSeries(cgmTodaySeries);

		// set other properties
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setViewPort(10, 1000000000);
		graphView.getGraphViewStyle().setNumVerticalLabels(14);
		graphView.setManualYAxisBounds(300, 40);
		graphView.setManualYAxis(true);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLUE);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph2);
		layout.addView(graphView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cgmgraph, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_cgmgraph,
					container, false);
			return rootView;
		}
	}

}
