package com.gafeol.dozeemdoze.util

// Replaces email dots with commas, so they can be saved to Firebase
fun cleanEmail(email : String) : String {
    return email.split('.').joinToString(",")
}

fun formatTime(minutes: Int): String {
    val hour = minutes/60
    val min = minutes%60
    val hourString = if (hour < 10) "0$hour" else "$hour"
    val minString = if(min < 10) "0$min" else "$min"
    return "$hourString:$minString"
}

