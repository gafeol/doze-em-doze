package com.gafeol.dozeemdoze

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gafeol.dozeemdoze.util.formatTime
import kotlinx.android.synthetic.main.activity_medication_view.*

class MedicationView : AppCompatActivity() {
    private var med = Medication("Nenhuma medicação selecionada!", R.drawable.ic_pills, 0, 24*60)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_view)
        intent.getBundleExtra("medication")?.let { med = it.unbundledMedication() }
        titleTextView.text = med.name
        medImageView.setImageResource(med.img)
        startTimeTextView.text = "Alarmes começam às ${formatTime(med.startingTime)}"
        frequencyTextView.text = "Repete a cada ${med.frequency/60} horas"
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
            return true
        }
        return false
    }

    private fun deleteMedication() {
        med.delete()
        finish()
    }
}