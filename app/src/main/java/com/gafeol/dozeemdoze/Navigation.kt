package com.gafeol.dozeemdoze

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.gafeol.dozeemdoze.util.isAuth
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_navigation.*
import java.util.*

class Navigation : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 123

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private val RC_OVERLAY_PERMISSION = 111

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
                .setIcon(android.R.drawable.ic_dialog_alert)
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

    private fun updateAuthButtons() {
        if(isAuth()) {
            signInButton.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
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

    fun onSignIn (v: View) {
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.Theme_DozeEmDoze_NoActionBar)
                        .setLogo(R.drawable.ic_logo_round)
                        .build(),
                RC_SIGN_IN)
    }

    fun saveUID(user : FirebaseUser) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
        val cleanEmail = user.email!!.split('.').joinToString(",")
        userRef.child(cleanEmail).setValue(user.uid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleOf(Pair("name", user!!.displayName), Pair("email", user.email)))
                updateAuthButtons()
                saveUID(user)
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode === RC_OVERLAY_PERMISSION) {
            if(!Settings.canDrawOverlays(this))
                createPermissionAlertDialog()
            else
                startMedications()
        }
    }

    private fun startMedications() {
        val intent = Intent(this, Medications::class.java).apply{}
        startActivity(intent)
        finish()
    }
}