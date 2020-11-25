package com.gafeol.dozeemdoze

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class Navegacao : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 123

    private fun updateAuthButtons() {
        var signInButton = findViewById<Button>(R.id.signInButton)

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            signInButton.visibility = View.GONE
            startMedicacoes()
        }
        else {
            signInButton.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegacao)
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
                .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Log.i("SIGN", "Success on sign in")
                updateAuthButtons()
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
    }

    private fun startMedicacoes() {
        val intent = Intent(this, Medicacoes::class.java).apply{}
        finish()
        startActivity(intent)
    }
}