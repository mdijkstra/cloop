package com.erjr.diabetesi1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.erjr.cloop.dao.AlertDataSource;
import com.erjr.cloop.entities.Alert;

public class AlertsManager extends BroadcastReceiver {

	private static int notificationId;

	public static void showAlerts(Context context) {
		AlertDataSource alertDS = new AlertDataSource(context);
		Alert[] alerts = alertDS.getAlertsToShow();
		if (alerts == null) {
			return;
		}
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		for (Alert alert : alerts) {
			// setup dismiss intent so can record when alerts are dismissed
			Intent dismissIntent = new Intent(context, AlertsManager.class);
			dismissIntent.setAction("com.erjr.diabetesi1.AlertManager");
			dismissIntent.putExtra("alertId", alert.getAlertId());
			PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
					context, 0, dismissIntent, 0);
			// Builder builder = new Notification.Builder(this):
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(alert.getType() + ": " + alert.getTitle())
					.setContentText(alert.getMessage());

			mBuilder.setDeleteIntent(dismissPendingIntent);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(notificationId, mBuilder.build());

		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int alertId = intent.getExtras().getInt("alertId");
		AlertDataSource alertDS = new AlertDataSource(context);
		alertDS.dismiss(alertId);
	}
}
