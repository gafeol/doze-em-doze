package com.gafeol.dozeemdoze

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.gafeol.dozeemdoze.util.sendNotification
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


        createChannel(
            getString(R.string.med_notification_channel_id),
            "Meds"
        )

        // TODO: Step 1.5 get an instance of NotificationManager and call sendNotification
        val notificationManager = ContextCompat.getSystemService(
            application,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification("Chegou a hora de tomar a sua medicação!", application)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medListView = findViewById<ListView>(R.id.medListView)
                var list = mutableListOf<Medication>()
                for(med in snapshot.children){
                    println(med)
                    val img  = med.child("img")?.value as Long
                    list.add(Medication(med.key!!, img?.toInt()))
                }
                val adapter = MedicationAdapter(applicationContext, list)
                medListView.setAdapter(adapter)
                medListView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                    override fun onItemClick(
                            parent: AdapterView<*>?, view: View?,
                            position: Int, id: Long
                    ) {
                        val intent = Intent(applicationContext, MedicationView::class.java).apply{}
                        val medicationBundle = list.get(position).bundle()
                        intent.putExtra("medication", medicationBundle)
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

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_LOW
            )
            // TODO: Step 2.6 disable badges for this channel

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Hora de tomar seus remédios!"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // TODO: Step 1.6 END create a channel

    }
}

