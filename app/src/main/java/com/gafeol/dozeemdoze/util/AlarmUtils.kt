package com.gafeol.dozeemdoze.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gafeol.dozeemdoze.receiver.AlarmReceiver
import java.util.*

fun minutesToday(c: Calendar = Calendar.getInstance()) = c.get(Calendar.HOUR_OF_DAY)*60 + c.get(Calendar.MINUTE)

fun setAlarm(context: Context, intent: Intent, time : Int){
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

    val alarmCalendar = GregorianCalendar().also{
        it.timeInMillis = System.currentTimeMillis()
        it.set(Calendar.HOUR_OF_DAY, time/60)
        it.set(Calendar.MINUTE, time%60)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
        if(time < minutesToday()) {
            it.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
    )
    Log.d("ALARM", "Set alarm RTC WAKEUP for timeinMilis ${alarmCalendar.timeInMillis}")
}