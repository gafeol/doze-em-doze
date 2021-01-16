package com.gafeol.dozeemdoze

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicationView : AppCompatActivity() {
    private val defaultMedication = Medication("Nenhuma medicação selecionada!", R.drawable.ic_pills, 0, 24*60)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_view)
        var med = defaultMedication
        intent.getBundleExtra("medication")?.let { med = it.unbundledMedication() }
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = med.name
        val medImageView = findViewById<ImageView>(R.id.medImageView)
        medImageView.setImageResource(med.img)
        // TODO: Add views for time and frequency as well
    }

    fun editMedication(view: View) {
        Toast.makeText(applicationContext, "Não implementado ainda", Toast.LENGTH_SHORT).show()
    }
}