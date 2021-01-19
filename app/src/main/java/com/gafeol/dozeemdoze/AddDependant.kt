package com.gafeol.dozeemdoze

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_add_dependant.*

class AddDependant : AppCompatActivity() {
    private var img = -1
    private lateinit var  mFirebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dependant)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun checkForm() : Boolean {
        var valid = true
        if(nameEditText.text.isEmpty()){
            nameEditText.error = "Digite o nome"
            valid = false
        }
        if(img == -1){
            iconTextView.error = "Escolha uma imagem"
            valid = false
        }
        return valid
    }


    private fun deselectImages() = imgLinearLayout.children.forEach { it.isSelected = false }

    fun chooseImage(view: View) {
        deselectImages()
        view.isSelected = true
        img = resources.getIdentifier(view.tag.toString(), "drawable", packageName)
    }


    fun saveDependant(view: View) {
        if(checkForm()){
            // TODO: save to firebase
        }
    }
}