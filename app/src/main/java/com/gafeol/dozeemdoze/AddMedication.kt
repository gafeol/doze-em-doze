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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.gafeol.dozeemdoze.receiver.AlarmReceiver
import com.gafeol.dozeemdoze.style.TimePicker24

class AddMedication : AppCompatActivity() {
    private var img : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
    }

    private fun checkForm() : Boolean {
        var valid = true
        var nameEditText = findViewById<EditText>(R.id.nameEditText)
        if(nameEditText.text.isEmpty()){
            nameEditText.error = "Digite o nome da medicação"
            valid = false
        }
        var medicationTypeTextView = findViewById<TextView>(R.id.medicationTypeTextView)
        if(img == -1){
            medicationTypeTextView.error = "Escolha uma imagem"
            valid = false
        }
        return valid
    }

    private fun getFrequency() : Int {
        var frequencySpinner = findViewById<Spinner>(R.id.frequencySpinner)
        return when (frequencySpinner.selectedItem.toString()) {
            "Todo dia" -> 24*60
            "12 em 12 horas" -> 12*60
            "8 em 8 horas" -> 8*60
            "6 em 6 horas" -> 6*60
            "4 em 4 horas" -> 4*60
            else -> throw Exception("Not known med frequency chosen")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun saveMedication(view: View) {
        if(checkForm()){
            var nameEditText = findViewById<EditText>(R.id.nameEditText)
            val medName = nameEditText.text.toString()
            val timePicker = findViewById<TimePicker24>(R.id.startTimePicker)
            val startMinute = timePicker.hour*60 + timePicker.minute
            val frequency = getFrequency()
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

    private fun deselectImages() = findViewById<LinearLayout>(R.id.imgLinearLayout).children.forEach { it.isSelected = false }

    fun chooseImage(view: View) {
        deselectImages()
        view.isSelected = true
        img = resources.getIdentifier(view.tag.toString(), "drawable", packageName)
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
                AlarmManager.INTERVAL_HALF_HOUR,
                alarmIntent
        )
    }
}
