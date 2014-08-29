package com.erjr.cloop.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BootStrapServices extends BroadcastReceiver {
	// http://stackoverflow.com/questions/7690350/android-start-service-on-boot
	public void onReceive(Context c, Intent intent) {
		Intent i = new Intent(c, BTSyncService.class);
		c.startService(i);
		Log.i("BootStrapBTSyncService", "Started");
		
//		Intent i2 = new Intent(c, ManageBGLSyncService.class);
//		c.startService(i2);
//		Log.i("BootStrapBTSyncService", "Started");
	}
}
