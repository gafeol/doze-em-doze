package com.gafeol.dozeemdoze

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gafeol.dozeemdoze.models.Dependant
import com.gafeol.dozeemdoze.models.DependantAdapter
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_medication_view.*
import com.google.firebase.database.ValueEventListener as ValueEventListener1

class MedicationView : AppCompatActivity() {
    private var med = Medication("Nenhuma medicação selecionada!", R.drawable.ic_pills, 0, 24*60, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_view)
        intent.getBundleExtra("medication")?.let { med = it.unbundledMedication() }
        titleTextView.text = med.name
        medImageView.setImageResource(med.img)

        startTimeTextView.text = nextAlarmMessage()
        frequencyTextView.text = "Repete a cada ${med.frequency/60} horas."


        // Renders the dependant that should take this med
        med.dependant?.let { dependantName ->
            listTitleTextView.visibility = View.VISIBLE
            getUserDBRef().child("dependants/$dependantName").addListenerForSingleValueEvent(object : ValueEventListener1 {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val depList = listOf(
                            Dependant(
                                    dependantName,
                                    snapshot.child("email").value as String?,
                                    (snapshot.child("img").value as Long).toInt(),
                                    snapshot.child("confirmation").value as Boolean
                            )
                    )
                    val adapter = DependantAdapter(applicationContext, depList)
                    dependantListView.adapter = adapter
                    //dependantListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Busca cancelada!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun nextAlarmMessage() : String {
        val nextAlarmIn = med.minutesToAlarm()
        val hours = nextAlarmIn/60
        val hourNoun = "hora" + if(hours > 1) "s" else ""
        val min = nextAlarmIn%60
        val minNoun = "minuto" + if(min > 1) "s" else ""
        if(hours > 0){
            if(min > 0)
                return "Próxima dose em $hours $hourNoun e $min $minNoun"
            else
                return "Próxima dose em $hours $hourNoun"
        }
        else {
            if(min > 0)
                return "Próxima dose em $min $minNoun"
            else
                return "Tomar sua dose agora"
        }
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