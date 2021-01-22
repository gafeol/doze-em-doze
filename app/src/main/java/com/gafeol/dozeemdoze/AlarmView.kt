package com.gafeol.dozeemdoze

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AlarmView : AppCompatActivity() {
    lateinit var ringtone : Ringtone
    private val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_view)
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        if(!ringtone.isPlaying)
            ringtone.play()
    }

    fun tookPills(v : View) {
        assert(ringtone.isPlaying)
        ringtone.stop()
        finish()
    }
}