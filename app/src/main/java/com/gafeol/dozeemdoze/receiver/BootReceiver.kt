package com.gafeol.dozeemdoze.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.gafeol.dozeemdoze.util.isAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class BootReceiver : BroadcastReceiver() {
    fun nextAlarmTime(time: Int) : Int {
        val cal = Calendar.getInstance()
        val minutesToday = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE)
        var nextAlarmTime = time - minutesToday
        if(nextAlarmTime < 0)
            nextAlarmTime += 24 * 60
        return nextAlarmTime
    }

    fun minutesToday(c: Calendar = Calendar.getInstance()) = c.get(Calendar.HOUR_OF_DAY)*60 + c.get(Calendar.MINUTE)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if(isAuth()){
                var timeNow = SystemClock.elapsedRealtime()
                timeNow = (timeNow/60000)*60000 // Removing seconds from timeNow
                getUserDBRef().child("alarms").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                        snapshot.children.forEach { timeSnap ->
                            val time = timeSnap.key!!.toInt()
                            // Using time as the request code for pending intents
                            val pendingIntent = PendingIntent.getService(context, time, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                            if (pendingIntent != null && alarmManager != null) {
                                alarmManager.cancel(pendingIntent)
                            }
                            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { alarmIntent ->
                                alarmIntent.putExtra("time", time)
                                PendingIntent.getBroadcast(context, time, alarmIntent, 0)
                            }


                            val alarmTime = timeNow + nextAlarmTime(time)*60*1000
                            alarmManager?.setRepeating(
                                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                    alarmTime, // TODO: Checar
                                    AlarmManager.INTERVAL_DAY,
                                    alarmIntent
                            )
                            Log.d("ALARM", "Set alarm for $time triggering at $alarmTime")
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }
}