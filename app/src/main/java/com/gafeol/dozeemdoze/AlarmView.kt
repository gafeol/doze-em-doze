package com.gafeol.dozeemdoze

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gafeol.dozeemdoze.receiver.SnoozeReceiver
import kotlinx.android.synthetic.main.activity_alarm_view.*

class AlarmView : AppCompatActivity() {
    lateinit var ringtone : Ringtone
    lateinit var vibrator : Vibrator
    private val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

    companion object {
        var activity : Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_view)
        playAlarm()
        setMedText()
    }

    private fun playAlarm(){
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        if(!ringtone.isPlaying)
            ringtone.play()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    private fun setMedText() {
        val meds = intent.getStringArrayExtra("meds")
        var msg : String = ""
        meds?.forEachIndexed{i, medName ->
            if(i == 0)
                msg = "Hora de tomar "
            else
                msg += ", "
            msg += medName
        }
        medsTextView.text = msg
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
        vibrator.cancel()
        SnoozeReceiver.setSnoozeAlarm(applicationContext, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator.cancel()
        ringtone.stop()
    }
}