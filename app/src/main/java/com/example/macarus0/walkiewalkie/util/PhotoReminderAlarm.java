package com.example.macarus0.walkiewalkie.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.view.WalkStatusActivity;

public class PhotoReminderAlarm extends BroadcastReceiver {

    private static final String TAG = "PhotoReminderAlarm";


    public static String ALARM_WALK_ID = "alarm_walk_id";
    private static String CHANNEL_ID = "photo_reminder";

    public static void setAlarm(Context context, long walkId, int alarmSeconds) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PhotoReminderAlarm.class);
        intent.putExtra(ALARM_WALK_ID, walkId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        long alarmWindowStartMillis = (alarmSeconds-30) * 1000;
        long alarmWindowSizeMillis = 60*1000;
        alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                alarmWindowStartMillis,
                 alarmWindowSizeMillis,
                 alarmIntent);
    }

    public static void cancelAlarm(Context context, long walkId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PhotoReminderAlarm.class);
        intent.putExtra(ALARM_WALK_ID, walkId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        Intent walkIntent = new Intent(context, WalkStatusActivity.class);
        walkIntent.putExtra(WalkStatusActivity.WALK_ID, intent.getLongExtra(ALARM_WALK_ID, -1));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, walkIntent, 0);
        Log.i(TAG, "onReceive: Received intent");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_a_photo_grey_24dp)
                .setContentTitle(context.getString(R.string.walk_photo_reminder))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.photo_reminder_notification_channel_name);
            String description = context.getString(R.string.photo_reminder_notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
