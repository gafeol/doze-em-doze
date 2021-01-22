package com.gafeol.dozeemdoze

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_medication.*

class AddMedication : AppCompatActivity() {
    private var img : Int = -1
    private var dependant : String? = null
    private lateinit var  mFirebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
        setPatientViews()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun setPatientViews() {
        getUserDBRef().child("dependants").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dependantList = mutableListOf<String>("Você mesmo")
                if(snapshot.exists() && snapshot.hasChildren()){
                    snapshot.children.forEach{dependantSnap ->
                        dependantList.add(dependantSnap.key.toString())
                    }
                    val arrayAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, dependantList);
                    patientSpinner.adapter = arrayAdapter
                    patientSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            dependant = if(p2 > 0) dependantList[p2] else null
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                    }
                    patientTextView.visibility = View.VISIBLE
                    patientSpinner.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun checkForm() : Boolean {
        var valid = true
        if(nameEditText.text.isEmpty()){
            nameEditText.error = "Digite o nome da medicação"
            valid = false
        }
        if(img == -1){
            medicationTypeTextView.error = "Escolha uma imagem"
            valid = false
        }
        return valid
    }

    private fun getFrequency() : Int {
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
            val medName = nameEditText.text.toString()
            val startMinute = startTimePicker.hour*60 + startTimePicker.minute
            val frequency = getFrequency()
            val med = Medication(
                medName,
                img,
                //dosage,
                //medicineType,
                startMinute,
                frequency,
                dependant
            )
            med.save()
            med.setAlarm(applicationContext, intent)
            val minutesToAlarm = med.minutesToAlarm()
            Toast.makeText(applicationContext, "Alarme tocará em ${minutesToAlarm/60} horas e ${minutesToAlarm%60} minutos, repetindo a cada ${frequency/60} horas", Toast.LENGTH_SHORT).show()
            val medBundle = med.bundle()
            medBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "medication")
            medBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, medName)
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, medBundle)
            finish()
        }
    }

    private fun deselectImages() = imgLinearLayout.children.forEach { it.isSelected = false }

    fun chooseImage(view: View) {
        deselectImages()
        view.isSelected = true
        img = resources.getIdentifier(view.tag.toString(), "drawable", packageName)
    }
}
