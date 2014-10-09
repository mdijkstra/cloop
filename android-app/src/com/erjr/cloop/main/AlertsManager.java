package com.erjr.cloop.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.erjr.cloop.dao.AlertDataSource;
import com.erjr.cloop.entities.Alert;
import com.erjr.main.R;

public class AlertsManager extends BroadcastReceiver {

	private static final Integer ALERT_ID_OFFSET = 1000;
	private static int notificationId;
	private static PendingIntent resultPendingIntent;

	public static void showAlerts(Context context) {
		AlertDataSource alertDS = new AlertDataSource(context);
		Alert[] alerts = alertDS.getAlertsToShow();
		if (alerts == null) {
			return;
		}
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultPendingIntent =
			    PendingIntent.getActivity(
			    context,
			    0,
			    resultIntent,
			    PendingIntent.FLAG_NO_CREATE
			);
		for (Alert alert : alerts) {
			showAlert(alert, context);
		}
	}
	
	private static void showAlert(Alert alert, Context context) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(alert.getTitle())
				.setContentText(alert.getMessage());
		
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		if(alert.getType().equalsIgnoreCase("critical")) {
			// ring phone even if in silent
			v.vibrate(2000);
			MediaPlayer mp = MediaPlayer.create(context,
					R.raw.alarm);
			mp.start();
			mp.start();
			v.vibrate(2000);
			long[] pattern = new long[3];
			pattern[0] = 500;
			pattern[1] = 500;
			pattern[2] = 500;
			v.vibrate(pattern, 2);
		}
		if(alert.getType().equalsIgnoreCase("warning")) {
			// vibrate phone
			// notify if not in silent mode
			MediaPlayer mp = MediaPlayer.create(context,
					R.raw.alarm);
			v.vibrate(1000);
			if(am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
				mp.start();
			}
		}
		
		
		mBuilder.setDeleteIntent(getDeleteIntent(context, alert));
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(alert.getAlertId(), mBuilder.build());
	}

	protected static PendingIntent getDeleteIntent(Context ctx, Alert alert) {
		Intent intent = new Intent(ctx, AlertsManager.class);
		intent.setAction("notification_cancelled");
		intent.putExtra("alertId", alert.getAlertId());
		return PendingIntent.getBroadcast(ctx, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
//	private static boolean isAlreadyAlerted(Context context, int id) {
//		Intent notificationIntent = new Intent(context, MainActivity.class);
//	    PendingIntent test = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_NO_CREATE);
//	    return test != null;
//	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlertsManager","dismissed!!");
		int alertId = intent.getExtras().getInt("alertId");
		AlertDataSource alertDS = new AlertDataSource(context);
		alertDS.dismiss(alertId);
	}

//private class NotificationHelper {
//    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
//
//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            aVariable = 0; // Do what you want here
//            unregisterReceiver(this);
//        }
//    };
//
//    public void showNotification(Context ctx, String text) {
//        Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
//        PendingIntent pendintIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
//        registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
//        Notification n = new Notification.Builder(mContext).
//          setContentText(text).
//          setDeleteIntent(pendintIntent).
//          build();
//        NotificationManager.notify(0, n);
//    }
//}
}
