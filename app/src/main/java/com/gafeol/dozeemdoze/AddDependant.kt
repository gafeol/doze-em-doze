package com.gafeol.dozeemdoze

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.gafeol.dozeemdoze.models.Dependant
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
        if(emailCheckBox.isChecked and emailEditText.text.isEmpty()){
            emailEditText.error = "Digite o email associado à conta de seu dependente"
            valid = false
        }
        return valid
    }


    private fun deselectImages() = imgLinearLayout.children.forEach { it.isSelected = false }


    private fun getDependant() : Dependant {
        return Dependant(
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                img,
                confirmationCheckBox.isChecked
        )
    }

    fun saveDependant(view: View) {
        if(checkForm()){
            getDependant().save()
            finish()
        }
    }

    fun chooseImage(view: View) {
        deselectImages()
        view.isSelected = true
        img = resources.getIdentifier(view.tag.toString(), "drawable", packageName)
    }

    fun toggleCheckBox(view: View) {
        if((view as CheckBox).isChecked()){
            emailEditText.visibility = View.VISIBLE
            confirmationCheckBox.visibility = View.VISIBLE
        }
        else{
            emailEditText.text.clear()
            emailEditText.visibility = View.GONE
            confirmationCheckBox.isChecked = false
            confirmationCheckBox.visibility = View.GONE
        }
    }
}