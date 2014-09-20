package com.erjr.cloop.main;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.erjr.cloop.dao.CoursesDataSource;
import com.erjr.cloop.dao.IOBDataSource;
import com.erjr.cloop.dao.InjectionDataSource;
import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.entities.Course;
import com.erjr.cloop.entities.IOB;
import com.erjr.cloop.entities.Injection;
import com.erjr.cloop.entities.SGV;
import com.erjr.main.R;

public class DayGraphActivity extends NavDrawerActivity implements
		OnItemSelectedListener {

	private GraphicalView mChart;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private TimeSeries mIobSeries;
	// private XYSeriesRenderer mCurrentRenderer;
	private Date graphStartTime;
	private Date graphEndTime;
	private Spinner dataOptionsSpinner;
	private TimeSeries mSgvSeries;
	private boolean showGraphNums = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_graph);
		setDrawer(R.id.navigation_drawer_fragment, R.id.day_graph_activity,
				NavDrawerActivity.NAV_POSITION_DAY_GRAPH);
		addSpinnerListener();
	}

	private void addSpinnerListener() {
		dataOptionsSpinner = (Spinner) findViewById(R.id.spinnerDayGraph);
		dataOptionsSpinner.setOnItemSelectedListener(this);
	}

	private void updateDisplay() {
		initDates();
		updateGraph();
		updateList();
		setTitle();
	}

	private void initDates() {
		Calendar c = Calendar.getInstance();
		if (graphStartTime == null || graphEndTime == null) {
			c.setTime(Util.getCurrentDateTime());
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);
			graphStartTime = c.getTime();
			c.add(Calendar.DATE, 1);
			graphEndTime = c.getTime();
		}
	}

	private void setTitle() {
		if (graphStartTime != null) {
			CharSequence title = "DayGraphActivity - "
					+ (graphStartTime.getMonth() + 1) + "/"
					+ graphStartTime.getDate() + "/" + graphStartTime.getYear();
			getActionBar().setTitle(title);
		}
	}

	private void initChart() {
		if (mIobSeries != null) {
			return;
		}
		mIobSeries = new TimeSeries("IOB");
		mSgvSeries = new TimeSeries("SGV");
		mDataset.addSeries(mIobSeries);
		mDataset.addSeries(mSgvSeries);
		XYSeriesRenderer mIobRenderer = new XYSeriesRenderer();
		mIobRenderer.setColor(Color.RED);
		mIobRenderer.setDisplayChartValues(showGraphNums);
		XYSeriesRenderer mSgvRenderer = new XYSeriesRenderer();
		mSgvRenderer.setColor(Color.BLUE);
		mSgvRenderer.setDisplayChartValues(showGraphNums);
		mRenderer.setLabelsTextSize(30);
		mSgvRenderer.setChartValuesTextSize(40);
		mIobRenderer.setChartValuesTextSize(40);
		mRenderer.addSeriesRenderer(0, mIobRenderer);
		mRenderer.addSeriesRenderer(1, mSgvRenderer);
		mRenderer.setShowGridX(true);
	}

	private void updateGraphData() {
		if (mIobSeries == null || mSgvSeries == null) {
			initChart();
		}
		IOBDataSource iDS = new IOBDataSource(getBaseContext());
		SGVDataSource sDS = new SGVDataSource(getBaseContext());
		List<SGV> sgvs = sDS.getByDateRange(graphStartTime, graphEndTime);
		List<IOB> iobs = iDS.getByDateRange(graphStartTime, graphEndTime);
		mIobSeries.clear();
		mSgvSeries.clear();
		if (iobs == null) {
			mIobSeries.add(graphStartTime, 0);
		} else {
			for (int i = iobs.size() - 1; i > -1; i--) {
				mIobSeries.add(iobs.get(i).getDatetimeIOB(), iobs.get(i)
						.getIobBg());
			}
		}
		if (sgvs == null) {
			mSgvSeries.add(graphStartTime, 0);
		} else {
			for (int i = sgvs.size() - 1; i > -1; i--) {
				mSgvSeries.add(sgvs.get(i).getDatetimeRecorded(), sgvs.get(i)
						.getSg());
			}
		}
	}

	protected void onResume() {
		super.onResume();
		updateDisplay();
	}

	private void updateGraph() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		initChart();
		updateGraphData();
		if (mChart == null) {
			mChart = ChartFactory.getTimeChartView(this, mDataset, mRenderer,
					"HH:mm");
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
		updateDisplay();
	}

	public void buttonPrev(View view) {
		Calendar c = Calendar.getInstance();
		c.setTime(graphStartTime);
		c.add(Calendar.DATE, -1);
		graphStartTime = c.getTime();
		c.setTime(graphEndTime);
		c.add(Calendar.DATE, -1);
		graphEndTime = c.getTime();
		updateDisplay();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Calendar c = Calendar.getInstance();
		c.setTime(graphStartTime);
		switch (position) {
		case 0:
			c.set(Calendar.HOUR_OF_DAY, 6);
			graphStartTime = c.getTime();
			c.set(Calendar.HOUR_OF_DAY, 22);
			graphEndTime = c.getTime();
			break;
		case 1:
			c.set(Calendar.HOUR_OF_DAY, 9);
			graphEndTime = c.getTime();
			c.add(Calendar.DATE, -1);
			c.set(Calendar.HOUR_OF_DAY, 20);
			graphStartTime = c.getTime();
			break;
		case 2:
			c.set(Calendar.HOUR_OF_DAY, 0);
			graphStartTime = c.getTime();
			c.add(Calendar.DATE, 1);
			graphEndTime = c.getTime();
			break;
		}
		updateDisplay();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

}
