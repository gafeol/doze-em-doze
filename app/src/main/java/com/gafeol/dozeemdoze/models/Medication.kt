package com.gafeol.dozeemdoze

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gafeol.dozeemdoze.receiver.AlarmReceiver
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.row_medication.view.*
import java.util.*

class Medication(val name: String,
                 val img: Int,
                 //val dosage : Double, // 0.5, 1, 2
                 //val type : String, // cp, dose
                 val startingTime : Int, // Specified on minutes 0 = 00:00, 61 = 01:01
                 val frequency : Int) { // Specified on minutes

    fun bundle() : Bundle {
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("img", img)
        //bundle.putDouble("dosage", dosage)
        //bundle.putString("type", type)
        bundle.putInt("startingTime", startingTime)
        bundle.putInt("frequency", frequency)
        return bundle
    }

    // Save medicine to firebase
    fun save() {
        val medRef = getUserDBRef().child("medication/$name")
        val singleUpdate = hashMapOf<String, Any>(
            "img" to img,
            "alarm/time" to startingTime,
            "alarm/frequency" to frequency
        )
        medRef.updateChildren(singleUpdate)
        //myRef.child("dosage").setValue(dosage)
        //myRef.child("type").setValue(type)
    }

    fun delete() {
        val medRef = getUserDBRef().child("medication/$name")
        medRef.removeValue()
    }

    fun nextAlarmTime() : Int {
        val cal = Calendar.getInstance()
        val minutesToday = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE)
        var nextAlarmTime = startingTime - minutesToday
        if(nextAlarmTime < 0)
            nextAlarmTime += 24 * 60
        nextAlarmTime = nextAlarmTime%frequency
        return nextAlarmTime
    }

    fun setAlarm(context : Context, intent: Intent){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { alarmIntent ->
            alarmIntent.putExtra("medName", name)
            PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
        }

        alarmManager?.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime()  + nextAlarmTime(),
                1000L * 60 * frequency,
                alarmIntent
        )
    }
}


fun medFromSnapshot(snap: DataSnapshot): Medication {
    Log.d("MED", "medFromSnapshot : ${snap.key}")
    val img : Int  = snap.child("img").value?.let { (it as Long).toInt() } ?: R.drawable.ic_broken_image
    val startingTime = snap.child("alarm/time").value as Long
    val frequency = snap.child("alarm/frequency").value as Long
    return Medication(
        snap.key!!,
        img,
        startingTime.toInt(),
        frequency.toInt()
    )
}

// Extension of Bundle to extract Medication
fun Bundle.unbundledMedication() : Medication {
    return Medication(
            this.getString("name")!!,
            this.getInt("img"),
            //this.getDouble("dosage")!!,
            //this.getString("type")!!,
            this.getInt("startingTime"),
            this.getInt("frequency")
    )
}

// Adapter of medList to an ArrayAdapter
class MedicationAdapter(context: Context, medList: List<Medication>)
    : ArrayAdapter<Medication>(context,  R.layout.row_medication, medList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val med = getItem(position)!!
        var holder = ViewHolder()
        val retView: View

        if(convertView == null){
            val inflater = LayoutInflater.from(context);
            retView = inflater.inflate(R.layout.row_medication, parent, false);
            holder.name = retView.medTitleTextView
            holder.img = retView.medImageView
            retView.tag = holder
        } else {
            retView = convertView
            holder = retView.tag as ViewHolder
        }
        holder.name.setText(med.name)
        holder.img.setImageResource(med.img)
        return retView
    }

    internal class ViewHolder {
        lateinit var name: TextView
        lateinit var img: ImageView
    }
}