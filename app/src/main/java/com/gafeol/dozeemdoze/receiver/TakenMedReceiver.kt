package com.gafeol.dozeemdoze.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.gafeol.dozeemdoze.AlarmView

class TakenMedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ALARM", "Taken med received")
        val notificationManager = ContextCompat.getSystemService(
                context,
        NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
        AlarmView.activity?.finish()
    }
}