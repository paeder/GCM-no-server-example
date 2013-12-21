package com.aedermedia.gcm_no_server_example.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.aedermedia.gcm_no_server_example.R;

/**
 * Created by paeder on 12/3/13.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        if (!extras.isEmpty()) {

            int nType = Integer.parseInt(extras.getString("nType"));

            switch (nType){

                case Config.SIMPLE_NOTIFICATION:
                {
                    String alertText = extras.getString("alertText");
                    String titleText = extras.getString("titleText");
                    String contentText = extras.getString("contentText");

                    sendNotification(alertText,titleText,contentText);
                }
                break;
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void sendNotification(String alertText, String titleText,String contentText) {

        Intent notificationIntent;
        notificationIntent = new Intent(this, com.aedermedia.gcm_no_server_example.ui.activities.MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("show_response", "show_response");

        PendingIntent intent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        builder.setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon)
                .setTicker(alertText)
                .setContentTitle(titleText)
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();

        Notification n = builder.build();
        notificationManager.notify(NOTIFICATION_ID,n);
    }
}
