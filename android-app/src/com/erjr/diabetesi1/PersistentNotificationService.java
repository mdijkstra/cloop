package com.erjr.diabetesi1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PersistentNotificationService extends BroadcastReceiver {

	private static final String TAG = "PersistentNotificationService";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Updating notification");
		int myNotificationId = intent.getExtras().getInt("myNotificationId");
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
		 .setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Latest BG").setContentText(
						"persistent "
								+ MyDateUtil.convertDateToString(MyDateUtil
										.getCurrentDateTime()));
		mBuilder.setOngoing(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(myNotificationId, mBuilder.build());

	}

}
