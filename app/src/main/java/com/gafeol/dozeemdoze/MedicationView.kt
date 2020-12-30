package com.gafeol.dozeemdoze

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MedicationView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_view)
        val med = intent.getBundleExtra("medication")!!.unbundleMedication()
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = med.name
        val medImageView = findViewById<ImageView>(R.id.medImageView)
        medImageView.setImageResource(med.img)
    }
}