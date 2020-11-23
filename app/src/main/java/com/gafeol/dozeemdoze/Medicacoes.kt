package com.gafeol.dozeemdoze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Medicacoes : AppCompatActivity() {
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val meds = FirebaseDatabase.getInstance().getReference(userUID + "/medication")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicacoes)
        setSupportActionBar(findViewById(R.id.toolbar))

        meds.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medTextView = findViewById<TextView>(R.id.medTextView)
                medTextView.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled meds")
            }
        })

        /* TODO: Create arraylist of medicine on firebase under user's uid
        val adapter = ArrayAdapter(this, R.layout.row_medicacao, meds.getChildren()))
        list = findViewById<View>(R.id.list) as ListView
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener() {
            fun onItemClick(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "You Clicked at " + web.get(+position),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        */

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{ v -> startAddMedication(v)}
    }

    fun startAddMedication(v: View){
        val intent = Intent(this, AdicionarMedicacao::class.java).apply{}
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
                    finish();
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
