package com.example.macarus0.walkiewalkie.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.view.WalkStatusActivity

class PhotoReminderAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        val walkIntent = Intent(context, WalkStatusActivity::class.java)
        walkIntent.putExtra(WalkStatusActivity.WALK_ID, intent.getLongExtra(ALARM_WALK_ID, -1))
        val pendingIntent = PendingIntent.getActivity(context, 0, walkIntent, 0)
        Log.i(TAG, "onReceive: Received intent")
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_a_photo_grey_24dp)
                .setContentTitle(context.getString(R.string.walk_photo_reminder))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(1, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.photo_reminder_notification_channel_name)
            val description = context.getString(R.string.photo_reminder_notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService<NotificationManager>(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    companion object {

        private const val TAG = "PhotoReminderAlarm"


        var ALARM_WALK_ID = "alarm_walk_id"
        private const val CHANNEL_ID = "photo_reminder"

        fun setAlarm(context: Context, walkId: Long, alarmSeconds: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, PhotoReminderAlarm::class.java)
            intent.putExtra(ALARM_WALK_ID, walkId)
            val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            val alarmWindowStartMillis = ((alarmSeconds - 30) * 1000).toLong()
            val alarmWindowSizeMillis = (60 * 1000).toLong()
            alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    alarmWindowStartMillis,
                    alarmWindowSizeMillis,
                    alarmIntent)
        }

        fun cancelAlarm(context: Context, walkId: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, PhotoReminderAlarm::class.java)
            intent.putExtra(ALARM_WALK_ID, walkId)
            val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.cancel(alarmIntent)
        }
    }
}
