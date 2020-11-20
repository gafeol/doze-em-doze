package com.gafeol.dozeemdoze

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.view.View
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: tornar classe de entrada quando usuário já está logado
class Medicacoes : AppCompatActivity() {

    val meds = FirebaseDatabase.getInstance().getReference("name")

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
                TODO("Not yet implemented")
            }
        })


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener{v -> startAddMedication(v)}
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
}