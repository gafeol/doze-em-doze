package com.gafeol.dozeemdoze.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun getUserDBRef(): DatabaseReference {
    val userUID = FirebaseAuth.getInstance().currentUser?.let {
        val uid = it.uid.toString()
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("$uid")
        return myRef
    }
    throw Exception("User not authenticated to perform this action!")
}

fun isAuth() = (FirebaseAuth.getInstance().currentUser != null)