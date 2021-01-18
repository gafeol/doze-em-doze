package com.gafeol.dozeemdoze

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.gafeol.dozeemdoze.style.TimePicker24
import com.google.firebase.analytics.FirebaseAnalytics

class AddMedication : AppCompatActivity() {
    private var img : Int = -1
    private lateinit var  mFirebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
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
            "Todo dia" -> 24 * 60
            "12 em 12 horas" -> 12 * 60
            "8 em 8 horas" -> 8 * 60
            "6 em 6 horas" -> 6 * 60
            "4 em 4 horas" -> 4 * 60
            "1 em 1 minuto" -> 1
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
            val med = Medication(
                medName,
                img,
                //dosage,
                //medicineType,
                startMinute,
                frequency
            )
            med.save()
            med.setAlarm(applicationContext, intent)
            val minutesToAlarm = med.nextAlarmTime()
            Toast.makeText(applicationContext, "Alarme tocará em ${minutesToAlarm/60} horas e ${minutesToAlarm%60} minutos, repetindo a cada ${frequency/60} horas", Toast.LENGTH_SHORT).show()
            val medBundle = med.bundle()
            medBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "medication")
            medBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, medName)
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, medBundle)
            finish()
        }
    }

    private fun deselectImages() = findViewById<LinearLayout>(R.id.imgLinearLayout).children.forEach { it.isSelected = false }

    fun chooseImage(view: View) {
        deselectImages()
        view.isSelected = true
        img = resources.getIdentifier(view.tag.toString(), "drawable", packageName)
    }
}
