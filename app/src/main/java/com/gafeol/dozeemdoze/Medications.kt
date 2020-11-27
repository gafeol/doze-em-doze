package com.gafeol.dozeemdoze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Medications : AppCompatActivity() {
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val dbRef = FirebaseDatabase.getInstance().getReference("$userUID/medication")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medications)
        setSupportActionBar(findViewById(R.id.toolbar))

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medListView = findViewById<ListView>(R.id.medListView)
                var list = mutableListOf<Medication>()
                for(med in snapshot.children){
                    println(med)
                    val img  = (med.child("img").value as Long)
                    list.add(Medication(med.key!!, img.toInt()))
                }
                val adapter = MedicationAdapter(applicationContext, list)
                medListView.setAdapter(adapter)
                medListView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                            parent: AdapterView<*>?, view: View?,
                            position: Int, id: Long
                    ) {
                        Toast.makeText(
                                applicationContext,
                                "You Clicked at " + list.get(position).name,
                                Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(applicationContext, MedicationView::class.java).apply{}
                        val medName = list.get(position).name
                        intent.putExtra("medName", medName)
                        startActivity(intent)
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled meds search")
            }
        })
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{ v -> startAddMedication(v)}
    }

    fun startAddMedication(v: View){
        val intent = Intent(this, AddMedication::class.java).apply{}
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            var message = data?.getStringExtra("MESSAGE");
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.medication_menu, menu)
        return true
    }

    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    val intent = Intent(applicationContext, Navigation::class.java)
                    finish();
                    startActivity(intent)
                }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signOutItem){
            signOut()
            return true;
        }
        return false;
    }
}

