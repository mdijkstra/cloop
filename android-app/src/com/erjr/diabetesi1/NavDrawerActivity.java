package com.erjr.diabetesi1;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

public class NavDrawerActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	
    private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	private int currentNavPosition = 0;
	
	public static final int NAV_POSITION_MAIN = 0;
	public static final int NAV_POSITION_ADD_COURSE = 1;
	public static final int NAV_POSITION_GRAPH = 2;
	public static final int NAV_POSITION_NIGHT = 3;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }
	protected void setDrawer(int navDrawerFragmentId, int activityId, int navposition) {
		currentNavPosition = navposition;
		mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(navDrawerFragmentId);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                navDrawerFragmentId,
                (DrawerLayout) findViewById(activityId));
	}
	

    @Override
    public void onNavigationDrawerItemSelected(int position) {
    	if(position == currentNavPosition) {
    		Util.toast(getBaseContext(), "Already on screen #"+position);
    		return;
    	}
    	Intent intent = null;
    	
    	switch(position) {
    	case NAV_POSITION_MAIN:
//    		intent = new Intent(this, MainActivity.class);
    		break;
    	case NAV_POSITION_ADD_COURSE:
    		intent = new Intent(this, AddCourseActivity.class);
    		break;
    	case NAV_POSITION_GRAPH:
    		intent = new Intent(this, GraphActivity.class);
    		break;
    	case NAV_POSITION_NIGHT:
    		intent = new Intent(this, NightActivity.class);
    		break;
    	case 4:
//    		intent = new Intent(this, MainActivity.class);
    		break;
    	case 5:
//    		intent = new Intent(this, MainActivity.class);
    		break;
    	case 6:
//    		intent = new Intent(this, MainActivity.class);
    		break;
    	case 7:
//    		intent = new Intent(this, MainActivity.class);
    		break;
    	}
    	if(intent == null) {
    		Util.toast(getBaseContext(), "Task at position #"+position+" coming soon :)");
    		return;
    	}
    	startActivity(intent);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.nav_option1);
                break;
            case 2:
                mTitle = getString(R.string.nav_option2);
                break;
            case 3:
                mTitle = getString(R.string.nav_option3);
                break;
            case 4:
                mTitle = getString(R.string.nav_option4);
                break;
            case 5:
                mTitle = getString(R.string.nav_option5);
                break;
            case 6:
                mTitle = getString(R.string.nav_option6);
                break;
            case 7:
                mTitle = getString(R.string.nav_option7);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
