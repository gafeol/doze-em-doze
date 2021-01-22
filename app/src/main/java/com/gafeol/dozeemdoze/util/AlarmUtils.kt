package com.gafeol.dozeemdoze.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.gafeol.dozeemdoze.receiver.AlarmReceiver
import java.util.*

fun minutesToday(c: Calendar = Calendar.getInstance()) = c.get(Calendar.HOUR_OF_DAY)*60 + c.get(Calendar.MINUTE)

fun setAlarm(context: Context, intent: Intent, time : Int){
    var timeNow = SystemClock.elapsedRealtime()
    timeNow = (timeNow/60000)*60000 // Removing seconds from timeNow
    val minutesToday = minutesToday()
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val pendingIntent = PendingIntent.getService(context, time, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    if (pendingIntent != null && alarmManager != null) {
        alarmManager.cancel(pendingIntent)
    }
    val alarmIntent = Intent(context, AlarmReceiver::class.java).let { alarmIntent ->
        alarmIntent.putExtra("time", time)
        alarmIntent.flags = Intent.FLAG_RECEIVER_FOREGROUND
        PendingIntent.getBroadcast(context, time, alarmIntent, 0)
    }

    val alarmAt = timeNow + ((24*60 + time - minutesToday)%(24*60))*60*1000L
    alarmManager?.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            alarmAt,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
    )
    Log.d("ALARM", "Set alarm for $time triggering at $alarmAt")
}