package com.gafeol.dozeemdoze

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.gafeol.dozeemdoze.receiver.AlarmReceiver
import com.gafeol.dozeemdoze.style.TimePicker24

class AddMedication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
        setAlarm()
    }

    private fun checkForm() : Boolean {
        var nameEditText = findViewById<EditText>(R.id.nameEditText)
        if(nameEditText.text.isEmpty()){
            nameEditText.setError("Digite o nome da medicação")
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun saveMedication(view: View) {
        if(checkForm()){
            var nameEditText = findViewById<EditText>(R.id.nameEditText)
            val medName = nameEditText.text.toString()
            val img = R.drawable.ic_pills // TODO: Escolher imagem baseado na imagem clicada
            val timePicker = findViewById<TimePicker24>(R.id.startTimePicker)
            val startMinute = timePicker.hour*60 + timePicker.minute
            val frequency = 24*60
            Medication(
                medName,
                img,
                //dosage,
                //medicineType,
                startMinute,
                frequency
            ).save()
        }
    }

    private fun setAlarm(){
        val context = applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        alarmManager?.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()  + 10 * 1000,
                AlarmManager.INTERVAL_HALF_HOUR/30,
                alarmIntent
        )
    }
}
