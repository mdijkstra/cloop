package com.erjr.cloop.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.erjr.main.R;

public class CgmGraphActivity extends NavDrawerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cgmgraph);
		setDrawer(R.id.navigation_drawer_fragment, R.id.graph_activity,
				NavDrawerActivity.NAV_POSITION_GRAPH);

//		// get SGV data and add to series
//		SGVDataSource SGVDS = new SGVDataSource(getBaseContext());
//		Calendar c = Calendar.getInstance();
//		c.setTime(Util.getCurrentDateTime());
//		c.set(Calendar.HOUR, 0);
//		c.set(Calendar.MINUTE, 0);
//		c.set(Calendar.SECOND, 0);
//		Date startTime = c.getTime();
//		c.add(Calendar.HOUR, 24);
//		Date endTime = c.getTime();
//		List<SGV> sgvs = SGVDS.getByDateRange(startTime, endTime);
//		if (sgvs == null) {
//			return;
//		}
//		GraphViewData[] cgmTodayData = new GraphViewData[sgvs.size()];
//		for (int i = 0; i < sgvs.size(); i++) {
//			cgmTodayData[i] = new GraphViewData(sgvs.get(i)
//					.getDatetimeRecorded().getTime(), sgvs.get(i).getSg());
//		}
//		GraphViewSeries cgmTodaySeries = new GraphViewSeries(cgmTodayData);
//
//		final java.text.DateFormat dateTimeFormatter = DateFormat
//				.getTimeFormat(getBaseContext());
//		LineGraphView graphView = new LineGraphView(this,
//				Util.convertDateToPrettyString(startTime) + "CGM Data") {
//			@Override
//			protected String formatLabel(double value, boolean isValueX) {
//				if (isValueX) {
//					// transform number to time for x-axis display
//					return Util
//							.convertDateToPrettyString(new Date((long) value));
//				} else {
//					return super.formatLabel(value, isValueX);
//				}
//			}
//		};
//
//		// add lines
//		graphView.addSeries(cgmTodaySeries);
//
//		// set other properties
//		graphView.setScrollable(true);
//		graphView.setScalable(true);
//		graphView.setViewPort(10, 1000000000);
//		graphView.getGraphViewStyle().setNumVerticalLabels(14);
//		graphView.setManualYAxisBounds(300, 40);
//		graphView.setManualYAxis(true);
//		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.RED);
//		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLUE);
//
//		LinearLayout layout = (LinearLayout) findViewById(R.id.graph2);
//		layout.addView(graphView);
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
}
