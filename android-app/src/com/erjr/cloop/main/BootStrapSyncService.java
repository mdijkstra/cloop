package com.erjr.cloop.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootStrapSyncService extends BroadcastReceiver {
	// http://stackoverflow.com/questions/7690350/android-start-service-on-boot
	public void onReceive(Context c, Intent intent) {
		Intent i = new Intent(c, BTSyncService.class);
		c.startService(i);
		Log.i("BootStrapBTSyncService", "Started");
	}
}
