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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.firebase.ui.auth.AuthUI
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_medications.*

class Medications : AppCompatActivity() {
    fun forceLightTheme() = AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    private lateinit var medEventListener : ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        forceLightTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medications)
        setSupportActionBar(toolbar)

        createChannel(
            getString(R.string.med_notification_channel_id),
            getString(R.string.med_notification_channel_name)
        )

        // Renders med list
        val medRef = getUserDBRef().child("medication")
        medEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var medList = mutableListOf<Medication>()
                snapshot.children.forEach{snap -> medList.add(medFromSnapshot(snap))}
                val adapter = MedicationAdapter(applicationContext, medList)
                medListView.adapter = adapter
                medListView.onItemClickListener = AdapterView.OnItemClickListener {
                    parent, view, position, id ->
                    val intent = Intent(applicationContext, MedicationView::class.java).apply{}
                    val medicationBundle = medList[position].bundle()
                    intent.putExtra("medication", medicationBundle)
                    startActivity(intent)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled meds search")
            }
        })
        medRef.addValueEventListener(medEventListener)
    }

    fun startAddMedication(v: View){
        val intent = Intent(this, AddMedication::class.java).apply{}
        startActivity(intent)
    }

    // MENU with sign out option

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.medications_menu, menu)
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
        return when (item.itemId) {
            R.id.signOutItem -> {
                signOut()
                true
            }
            R.id.addDependent -> {
                val addDepIntent = Intent(applicationContext, AddDependant::class.java).apply {}
                startActivity(addDepIntent)
                true
            }
            else -> false
        }
    }

    // NOTIFICATIONS

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Canal para avisar horários de medicação"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

