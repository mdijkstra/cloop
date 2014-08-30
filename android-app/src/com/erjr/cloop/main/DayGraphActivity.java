package com.erjr.cloop.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.Injection;
import com.erjr.main.R;

public class DayGraphActivity extends NavDrawerActivity implements
		OnItemSelectedListener {

	private GraphicalView mChart;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mIobSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private Date graphStartTime;
	private Date graphEndTime;
	private Spinner dataOptionsSpinner;
	private XYSeries mSgvSeries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_graph);
		setDrawer(R.id.navigation_drawer_fragment, R.id.day_graph_activity,
				NavDrawerActivity.NAV_POSITION_DAY_GRAPH);
		addSpinnerListener();
		initDates();
		updateGraph();
		updateList();
	}

	private void addSpinnerListener() {
		dataOptionsSpinner = (Spinner) findViewById(R.id.spinnerDayGraph);
		dataOptionsSpinner.setOnItemSelectedListener(this);
	}

	private void initDates() {
		Calendar c = Calendar.getInstance();
		if (graphStartTime == null || graphEndTime == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR, 0);
			graphStartTime = c.getTime();
			c.add(Calendar.DATE, 1);
			graphEndTime = c.getTime();
		}
	}

	private void initChart() {
		if (mIobSeries != null) {
			return;
		}
		mIobSeries = new XYSeries("IOB");
		mSgvSeries = new XYSeries("SGV");
		mDataset.addSeries(mIobSeries);
		mDataset.addSeries(mSgvSeries);
		mCurrentRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(mCurrentRenderer);
		mRenderer.addSeriesRenderer(mCurrentRenderer);
	}

	private void updateData() {
		mIobSeries.add(1, 2);
		mIobSeries.add(2, 3);
		mIobSeries.add(3, 2);
		mIobSeries.add(4, 5);
		mIobSeries.add(5, 4);
		mSgvSeries.add(1, 1);
		mSgvSeries.add(2, 2);
		mSgvSeries.add(3, 3);
		mSgvSeries.add(4, 4);
		mSgvSeries.add(5, 5);
	}

	protected void onResume() {
		super.onResume();
		updateGraph();
		updateList();
	}

	private void updateGraph() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		initChart();
		updateData();
		if (mChart == null) {
			mChart = ChartFactory.getCubeLineChartView(this, mDataset,
					mRenderer, 0.3f);
			layout.addView(mChart);
		} else {
			mChart.repaint();
		}
	}

	private void clearList() {
		String[] emptyStrings = new String[1];
		emptyStrings[0] = "";
		updateList(emptyStrings);
	}

	private void updateList(String[] items) {
		if (items == null) {
			clearList();
			return;
		}
		ListView list = (ListView) findViewById(R.id.listData);
		// defining Adapter for List content
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		for (Object item : items) {
			adapter.add(item.toString());
		}
		list.setAdapter(adapter);
	}

	private void updateList() {
		CoursesDataSource cDS = new CoursesDataSource(getBaseContext());
		InjectionDataSource iDS = new InjectionDataSource(getBaseContext());
		List<Course> courses = cDS.getByDateRange(graphStartTime, graphEndTime);
		List<Injection> injections = iDS.getInjectionsByDateRange(
				graphStartTime, graphEndTime);
		int size = 0;
		if (courses != null && !courses.isEmpty()) {
			size += courses.size();
		}
		if (injections != null && !injections.isEmpty()) {
			size += injections.size();
		}
		if (size == 0) {
			clearList();
			Util.toast(
					getBaseContext(),
					"Nothing to show : "
							+ Util.convertDateToPrettyString(graphStartTime)
							+ " - "
							+ Util.convertDateToPrettyString(graphEndTime));
			return;
		}
		String[] strings = new String[size];
		if (courses != null) {
			for (int i = 0; i < courses.size(); i++) {
				strings[i] = courses.get(i).toString();
			}
		}
		int j = 0;
		if (courses != null) {
			j = courses.size();
		}
		if (injections != null) {
			for (int i = 0; i < injections.size(); i++) {
				strings[j + i] = injections.get(i).toString();
			}
		}
		Util.toast(
				getBaseContext(),
				Util.convertDateToPrettyString(graphStartTime) + " - "
						+ Util.convertDateToPrettyString(graphEndTime));
		updateList(strings);
	}

	public void buttonNext(View view) {
		Calendar c = Calendar.getInstance();
		c.setTime(graphStartTime);
		c.add(Calendar.DATE, 1);
		if (c.getTime().before(Util.getCurrentDateTime())) {
			graphStartTime = c.getTime();
			c.setTime(graphEndTime);
			c.add(Calendar.DATE, 1);
			graphEndTime = c.getTime();
		}
		updateGraph();
		updateList();
	}

	public void buttonPrev(View view) {
		Calendar c = Calendar.getInstance();
		c.setTime(graphStartTime);
		c.add(Calendar.DATE, -1);
		graphStartTime = c.getTime();
		c.setTime(graphEndTime);
		c.add(Calendar.DATE, -1);
		graphEndTime = c.getTime();
		updateGraph();
		updateList();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Calendar c = Calendar.getInstance();
		c.setTime(graphStartTime);
		switch (position) {
		case 0:
			c.set(Calendar.HOUR, 6);
			graphStartTime = c.getTime();
			c.set(Calendar.HOUR, 22);
			graphEndTime = c.getTime();
			break;
		case 1:
			c.set(Calendar.HOUR, 9);
			graphEndTime = c.getTime();
			c.add(Calendar.DATE, -1);
			c.set(Calendar.HOUR, 20);
			graphStartTime = c.getTime();
			break;
		case 2:
			c.set(Calendar.HOUR, 0);
			graphStartTime = c.getTime();
			c.add(Calendar.DATE, 1);
			graphEndTime = c.getTime();
			break;
		}
		updateGraph();
		updateList();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

}
