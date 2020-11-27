package com.gafeol.dozeemdoze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MedicationView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_view)
        val medName = intent.getStringExtra("medName")
        if (medName != null) {
            Log.d("MEDNAME", medName)
        }
    }
}