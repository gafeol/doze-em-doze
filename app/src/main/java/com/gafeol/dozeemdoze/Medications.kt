package com.gafeol.dozeemdoze

import android.app.Notification
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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_medications.*
import kotlinx.android.synthetic.main.app_bar_medications.*
import kotlinx.android.synthetic.main.content_medications.*
import kotlinx.android.synthetic.main.nav_header.view.*

class Medications : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
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

            // startActivity(Intent(applicationContext, AlarmView::class.java))
        // Renders med list
        val medRef = getUserDBRef().child("medication")
        medEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var medList = mutableListOf<Medication>()
                snapshot.children.forEach{snap -> medList.add(Medication(snap))}
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

        // Drawer layout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_menu, R.string.close_menu)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        if(navView != null){
            navView.setNavigationItemSelectedListener(this)
            navView.menu.findItem(R.id.nav_medications).isChecked = true
            FirebaseAuth.getInstance().currentUser?.let { user ->
                navView.getHeaderView(0).helloTextView.text = "Olá ${user.displayName}!"
            }
        }
    }

    fun startAddMedication(v: View){
        val intent = Intent(this, AddMedication::class.java).apply{}
        startActivity(intent)
    }

    // MENU with sign out option
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lists_menu, menu)
        return true
    }

    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    val intent = Intent(applicationContext, Navigation::class.java)
                    startActivity(intent)
                    finish()
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signOutItem -> {
                signOut()
                true
            }
            R.id.addMedication -> {
                val addMedIntent = Intent(applicationContext, AddMedication::class.java)
                startActivity(addMedIntent)
                true
            }
            R.id.addDependant -> {
                val addDepIntent = Intent(applicationContext, AddDependant::class.java)
                startActivity(addDepIntent)
                true
            }
            R.id.settings -> {
               val settingsIntent = Intent(applicationContext, Settings::class.java)
                startActivity(settingsIntent)
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
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Nav Drawer
    override fun onBackPressed() {
        if(drawerLayout?.isDrawerOpen(GravityCompat.START) == true)
           drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_medications -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_alarms -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                val intent = Intent(applicationContext, Alarms::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.nav_dependants -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                val intent = Intent(applicationContext, Dependants::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.nav_settings -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                startActivity(Intent(applicationContext, Settings::class.java))
                true
            }
            R.id.nav_sign_out -> {
                signOut()
                true
            }
            else -> false
        }
    }
}

