package com.gafeol.dozeemdoze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdicionarMedicacao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_medicacao)
    }

    private val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    fun saveMedication(view: View) {
        var nameEditText = findViewById<EditText>(R.id.nameEditText)
        val db = FirebaseDatabase.getInstance()
        val medName = nameEditText.text.toString()
        val myRef = db.getReference("$userUID/medication/$medName")
        myRef.setValue(true).addOnSuccessListener { finish() }
    }
}
