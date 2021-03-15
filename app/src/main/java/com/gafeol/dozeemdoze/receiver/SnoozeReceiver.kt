package com.gafeol.dozeemdoze.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.gafeol.dozeemdoze.AlarmView
import java.util.*

class SnoozeReceiver: BroadcastReceiver() {
    companion object {
        fun setSnoozeAlarm(context : Context, intent: Intent) {
            Log.d("SNOOZE", "Setting snooze alarm")
            val notifyIntent = Intent(context, AlarmReceiver::class.java)
            notifyIntent.putExtra("meds", intent.getStringArrayExtra("meds"))
            notifyIntent.putExtra("time", intent.getIntExtra("time", 0))
            val notifyPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmCalendar = GregorianCalendar().also{
                it.timeInMillis = System.currentTimeMillis()
                it.add(Calendar.MINUTE, 1)
                it.set(Calendar.SECOND, 0)
                it.set(Calendar.MILLISECOND, 0)
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    alarmCalendar.timeInMillis,
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
