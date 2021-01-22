package com.gafeol.dozeemdoze.models

import com.gafeol.dozeemdoze.util.getUserDBRef

class Dependant(val name: String,
               val email: String?,
                val img: Int) {

    // Save dependant to firebase
    fun save() {
        val depRef = getUserDBRef().child("dependant/$name")
        var singleUpdate = mutableMapOf<String, Any>(
                "email" to (email ?: ""),
                "img" to img
        )
        depRef.setValue(singleUpdate)
        // TODO: propagate save on dependant`s uid tag as well, only if they have an email
    }

    fun delete() {
        val depRef = getUserDBRef().child("dependant/$name")
        depRef.removeValue()
    }

}