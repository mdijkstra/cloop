package com.erjr.diabetesi1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ManageBGLSyncService extends Service {

	private ManageBGLThread syncThread;
//	public BTSyncService(String name) {
//		super(name);
//		// TODO Auto-generated constructor stub
//	}

	public static final String TAG = "ManageBGLSyncService";
	
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		start();
		return 0;
	}
	
	public void onStart(Intent intent, int startId) {
		start();
	}
	
	private void start() {
		if(syncThread == null) {
			syncThread = new ManageBGLThread(getBaseContext());
		}
		if(!syncThread.isAlive()) {
			syncThread.start();
		}
	}
	
	public void onDestroy() {
		syncThread.cancel();
	}

//	@Override
//	protected void onHandleIntent(Intent intent) {
//		// TODO Auto-generated method stub
//		
//	}
	
}
