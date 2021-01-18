package com.gafeol.dozeemdoze

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicationView : AppCompatActivity() {
    private var med = Medication("Nenhuma medicação selecionada!", R.drawable.ic_pills, 0, 24*60)

    fun formatTime(minutes: Int): String {
        val hour = minutes/60
        val min = minutes%60
        val hourString = if (hour < 10) "0$hour" else "$hour"
        val minString = if(min < 10) "0$min" else "$min"
        return hourString + ":" + minString
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_view)
        intent.getBundleExtra("medication")?.let { med = it.unbundledMedication() }
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = med.name
        val medImageView = findViewById<ImageView>(R.id.medImageView)
        medImageView.setImageResource(med.img)

        val timeTextView = findViewById<TextView>(R.id.startTimeTextView)
        timeTextView.text = "Alarmes começam às " + formatTime(med.startingTime)
        val frequencyTextView = findViewById<TextView>(R.id.frequencyTextView)
        frequencyTextView.text = "Repete a cada " + (med.frequency/60).toString() + " horas"
    }

    fun editMedication(view: View) {
        Toast.makeText(applicationContext, "Não implementado ainda", Toast.LENGTH_SHORT).show()
    }


    // MENU with delete med option
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.medication_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteItem){
            deleteMedication()
            return true;
        }
        return false;
    }

    private fun deleteMedication() {
        med.delete()
        finish()
    }
}