package com.gafeol.dozeemdoze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddMedication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
    }

    private val userUID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private fun checkForm() : Boolean {
        var nameEditText = findViewById<EditText>(R.id.nameEditText)
        if(nameEditText.text.isEmpty()){
            nameEditText.setError("Digite o nome da medicação")
            return false
        }
        return true
    }

    fun saveMedication(view: View) {
        if(checkForm()){
            var nameEditText = findViewById<EditText>(R.id.nameEditText)
            val db = FirebaseDatabase.getInstance()
            val medName = nameEditText.text.toString()
            val myRef = db.getReference("$userUID/medication/$medName")
            myRef.setValue(true).addOnSuccessListener { finish() }
            myRef.child("img").setValue(R.drawable.ic_pills)
        }
    }
}
