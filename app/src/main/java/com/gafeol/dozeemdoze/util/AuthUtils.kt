package com.gafeol.dozeemdoze.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun getDBRef() = FirebaseDatabase.getInstance().reference

fun getUserDBRef(): DatabaseReference {
    FirebaseAuth.getInstance().currentUser?.let {
        val uid = it.uid
        val db = FirebaseDatabase.getInstance()
        return db.getReference(uid)
    }
    throw Exception("User not authenticated to perform this action!")
}

fun getUserEmail() = FirebaseAuth.getInstance().currentUser?.email

fun isAuth() = (FirebaseAuth.getInstance().currentUser != null)