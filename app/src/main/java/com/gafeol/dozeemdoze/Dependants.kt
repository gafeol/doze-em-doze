package com.gafeol.dozeemdoze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.gafeol.dozeemdoze.models.Dependant
import com.gafeol.dozeemdoze.models.DependantAdapter
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dependants.*
import kotlinx.android.synthetic.main.app_bar_medications.*
import kotlinx.android.synthetic.main.content_dependants.*
import kotlinx.android.synthetic.main.nav_header.view.*

class Dependants : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dependants)
        setSupportActionBar(toolbar)

        // Renders dep list
        val dependantRef = getUserDBRef().child("dependants")
        val dependantEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dependantList = mutableListOf<Dependant>()
                snapshot.children.forEach{snap -> dependantList.add(Dependant(snap))}
                val adapter = DependantAdapter(applicationContext, dependantList)
                dependantListView.adapter = adapter
                dependantListView.onItemClickListener = AdapterView.OnItemClickListener {
                    parent, view, position, id ->
                    Toast.makeText(applicationContext, "Nao implementado", Toast.LENGTH_SHORT).show()
                    //val intent = Intent(applicationContext, DependantView::class.java).apply{}
                    //val dependanticationBundle = dependantList[position].bundle()
                    //intent.putExtra("dependant", dependanticationBundle)
                    //startActivity(intent)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled dependants search")
            }
        })
        dependantRef.addValueEventListener(dependantEventListener)

        // Drawer layout
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_menu, R.string.close_menu)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        if(navView != null){
            navView.setNavigationItemSelectedListener(this)
            navView.menu.findItem(R.id.nav_dependants).isChecked = true
            FirebaseAuth.getInstance().currentUser?.let { user ->
                navView.getHeaderView(0).helloTextView.text = "Olá ${user.displayName}!"
            }
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
                val intent = Intent(applicationContext, Alarms::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.nav_dependants -> {
                drawerLayout.closeDrawer(GravityCompat.START)
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

    fun startAddDependant(v: View){
        val intent = Intent(this, AddDependant::class.java).apply{}
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
                val addMedIntent = Intent(applicationContext, AddMedication::class.java).apply {}
                startActivity(addMedIntent)
                true
            }
            R.id.addDependant -> {
                val addDepIntent = Intent(applicationContext, AddDependant::class.java).apply {}
                startActivity(addDepIntent)
                true
            }
            else -> false
        }
    }
}