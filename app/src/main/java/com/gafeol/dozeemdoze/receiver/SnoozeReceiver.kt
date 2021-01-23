package com.gafeol.dozeemdoze.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.gafeol.dozeemdoze.AlarmView

class SnoozeReceiver: BroadcastReceiver() {
    companion object {
        fun setSnoozeAlarm(context : Context, intent: Intent) {
            Log.d("SNOOZE", "Setting snooze alarm")
            val triggerTime = SystemClock.elapsedRealtime() + 5*DateUtils.MINUTE_IN_MILLIS
            val notifyIntent = Intent(context, AlarmReceiver::class.java)
            val notifyPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notifyPendingIntent
            )

            val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
            ) as NotificationManager
            notificationManager.cancelAll()
            AlarmView.activity?.finish()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        setSnoozeAlarm(context, intent)
    }
}
