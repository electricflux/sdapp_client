package com.parking.reminder;

import com.parking.dashboard.R;
import com.parking.timewidget.TimeActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ParkingReminderReceiver extends BroadcastReceiver {
	NotificationManager nm;
	private static final int NOTIFY_1 = 0x1001;
	private static final String TAG = ParkingReminderReceiver.class.getSimpleName();

	  @Override
	  public void onReceive(Context context, Intent intent)
	  {
	     //notificationStatus(context);
		  Log.v(TAG, "Alarm receiver: ");
		  nm = (NotificationManager) context
				    .getSystemService(Context.NOTIFICATION_SERVICE);
		  CharSequence from = "Parking";
		  CharSequence message = "Reminder";
		  Intent intent1 = new Intent(context, ParkingReminder.class);
		  PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
						    intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		  Notification notif = new Notification(R.drawable.icon,
						    "quickpark parking expiration reminder", System.currentTimeMillis());
		  notif.flags |= Notification.FLAG_AUTO_CANCEL;
		  notif.setLatestEventInfo(context, from, message, contentIntent);
		  nm.notify(NOTIFY_1, notif);
		  /**Cancel the alarm**/
		  
      	  PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		  AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		  alarmManager.cancel(pendingIntent);
			
		  Log.v(TAG, "Alarm receiver End: ");
	  }



	
}

