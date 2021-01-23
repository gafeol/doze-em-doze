package com.gafeol.dozeemdoze

import android.app.Activity
import android.app.NotificationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gafeol.dozeemdoze.receiver.SnoozeReceiver

class AlarmView : AppCompatActivity() {
    lateinit var ringtone : Ringtone
    private val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

    companion object {
        var activity : Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_view)
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        if(!ringtone.isPlaying)
            ringtone.play()
    }

    fun tookPills(v : View) {
        assert(ringtone.isPlaying)
        ringtone.stop()
        val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll() // Não realmente cancela os próximos alarmes. (Checar se cancela pro dia seguinte)
        finish()
    }

    fun snoozePills(view: View) {
        assert(ringtone.isPlaying)
        ringtone.stop()
        SnoozeReceiver.setSnoozeAlarm(applicationContext, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
    }
}