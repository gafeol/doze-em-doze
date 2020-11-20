package com.gafeol.dozeemdoze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase

class AdicionarMedicacao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_medicacao)
    }

    fun saveMedication(view: View) {
        var nameEditText = findViewById<EditText>(R.id.nameEditText)
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("name")
        myRef.setValue(nameEditText.text.toString()).addOnSuccessListener { finish() }
    }
}