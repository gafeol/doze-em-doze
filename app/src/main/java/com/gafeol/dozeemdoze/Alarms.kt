package com.gafeol.dozeemdoze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.gafeol.dozeemdoze.models.Alarm
import com.gafeol.dozeemdoze.models.AlarmAdapter
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_alarms.*
import kotlinx.android.synthetic.main.app_bar_medications.*
import kotlinx.android.synthetic.main.content_alarms.*

class Alarms : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarms)
        setSupportActionBar(toolbar)

        // Renders alarm list
        val alarmRef = getUserDBRef().child("alarms")
        val alarmEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var alarmList = mutableListOf<Alarm>()
                snapshot.children.forEach{snap -> alarmList.add(Alarm(snap))}
                val adapter = AlarmAdapter(applicationContext, alarmList)
                alarmListView.adapter = adapter
                alarmListView.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    Toast.makeText(applicationContext, "Nao implementado", Toast.LENGTH_SHORT).show()
                    //val intent = Intent(applicationContext, AlarmView::class.java).apply{}
                    //val alarmicationBundle = alarmList[position].bundle()
                    //intent.putExtra("alarm", alarmicationBundle)
                    //startActivity(intent)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled alarms search")
            }
        })
        alarmRef.addValueEventListener(alarmEventListener)

        // Drawer layout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_menu, R.string.close_menu)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        if(navView != null){
            navView.setNavigationItemSelectedListener(this)
            navView.menu.findItem(R.id.nav_alarms).isChecked = true
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
                val medIntent = Intent(applicationContext, Medications::class.java)
                startActivity(medIntent)
                finish()
                true
            }
            R.id.nav_alarms -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_dependants -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                val depIntent = Intent(applicationContext, Dependants::class.java)
                startActivity(depIntent)
                true
            }
            R.id.nav_sign_out -> {
                signOut()
                true
            }
            else -> false
        }
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
            R.id.addDependent -> {
                val addDepIntent = Intent(applicationContext, AddDependant::class.java).apply {}
                startActivity(addDepIntent)
                true
            }
            else -> false
        }
    }
}

