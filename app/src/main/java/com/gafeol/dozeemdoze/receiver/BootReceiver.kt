package com.gafeol.dozeemdoze.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.gafeol.dozeemdoze.util.isAuth
import com.gafeol.dozeemdoze.util.setAlarm
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if(isAuth()){
                getUserDBRef().child("alarms").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { timeSnap ->
                            val time = timeSnap.key!!.toInt()
                            setAlarm(context, intent, time)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
    }
}