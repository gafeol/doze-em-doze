package com.gafeol.dozeemdoze

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.gafeol.dozeemdoze.util.getUserDBRef

class Settings : AppCompatActivity() {
    companion object {
       lateinit var sharedPreferences : SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            when(preference?.key) {
                "confirmation" -> {
                    val confirmation : Boolean = sharedPreferences.getBoolean("confirmation", false)
                    super.onPreferenceTreeClick(preference)
                    if(confirmation)
                        getUserDBRef().child("confirmation").setValue(true)
                    else
                        getUserDBRef().child("confirmation").removeValue()
                }
                "sound" -> {
                    Toast.makeText(context, "Ainda não implementado", Toast.LENGTH_SHORT).show()
                }
                "notification" -> {
                    Toast.makeText(context, "Ainda não implementado", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Clicked on ${preference?.key} ", Toast.LENGTH_SHORT).show()
                }
            }
            return super.onPreferenceTreeClick(preference)
        }
    }
}