package com.gafeol.dozeemdoze

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Log.INFO
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.gafeol.dozeemdoze.util.cleanEmail
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.gafeol.dozeemdoze.util.isAuth
import com.gafeol.dozeemdoze.util.setAlarm
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_navigation.*
import java.util.*
import java.util.logging.Level.INFO

class Navigation : AppCompatActivity() {
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private val RC_OVERLAY_PERMISSION = 111
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun getOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                var getPermissionsIntent  : Intent
                if(isXiaomi()){
                    getPermissionsIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                            .setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                            .putExtra("extra_pkgname", packageName)
                }
                else {
                    getPermissionsIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                }
                startActivityForResult(getPermissionsIntent, RC_OVERLAY_PERMISSION)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createPermissionAlertDialog() {
        if (Settings.canDrawOverlays(applicationContext))
            return
        val builder = AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black)
                .setCancelable(false)
                .setPositiveButton("Permitir") { dialog, p1 ->
                    dialog?.cancel()
                    getOverlayPermission()
                }
                .setNegativeButton("Não permitir") { dialog, p1 ->
                    dialog?.cancel()
                    startMedications()
                }
        if(isXiaomi())
            builder.setMessage("Para usar alarmes sonoros é necessário dar as permissões \"Exibir janelas pop-up\" e \"Mostrar janelas pop-up enquanto estiver executando em segundo plano\" para este aplicativo.")
        else
            builder.setMessage("Para usar alarmes sonoros é necessário dar a permissão de \"Exibir janelas pop-up\" para este aplicativo.")
        val dialog: AlertDialog = builder.create()
        dialog.setTitle("Permitir uso de alarmes?")
        dialog.show()
    }

    private fun isXiaomi() : Boolean = ("xiaomi" == Build.MANUFACTURER.toLowerCase(Locale.ROOT))

    private fun hasAlarmsPermission() : Boolean = (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this))

    private fun updateAuthButtons() {
        if(isAuth()) {
            signInButton.visibility = View.GONE
            if (!hasAlarmsPermission())
                createPermissionAlertDialog()
            else
                startMedications()
        }
        else {
            signInButton.visibility = View.VISIBLE
        }
    }

    private fun forceLightTheme() = AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    override fun onCreate(savedInstanceState: Bundle?) {
        forceLightTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        updateAuthButtons()
    }

    override fun onResume() {
        super.onResume()
        updateAuthButtons()
    }

    fun onSignIn (v: View) {
        // Choose authentication providers
        // https://firebase.google.com/docs/auth/android/firebaseui#kotlin+ktx
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    fun saveUID(user : FirebaseUser) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
        val cleanEmail = cleanEmail(user.email!!)
        userRef.child(cleanEmail).setValue(user.uid)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleOf(Pair("name", user!!.displayName), Pair("email", user.email)))
            updateAuthButtons()
            saveUID(user)
            updateAlarms()
            startMedications()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Log.e("SIGN", "Error trying to sign-in")
            if (response != null) {
                Log.e("SIGN", response.error?.errorCode.toString())
            }
        }
    }

    private fun updateAlarms() {
        getUserDBRef().child("alarms").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { timeSnap ->
                    val time = timeSnap.key!!.toInt()
                    setAlarm(applicationContext, time)
                    Log.d("ALARM", "Set alarm for time $time")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun startMedications() {
        val intent = Intent(this, Medications::class.java).apply{}
        startActivity(intent)
        finish()
    }
}