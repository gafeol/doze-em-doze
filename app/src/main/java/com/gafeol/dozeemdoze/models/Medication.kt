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
                 val frequency : Int, // Specified on minutes
                 val dependant: String? = null) {

    fun bundle() : Bundle {
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("img", img)
        //bundle.putDouble("dosage", dosage)
        //bundle.putString("type", type)
        bundle.putInt("startingTime", startingTime)
        bundle.putInt("frequency", frequency)
        bundle.putString("dependant", dependant)
        return bundle
    }

    private fun alarmHash() : MutableMap<String, Any> {
        var map = mutableMapOf<String, Any>()
        val schedule = alarmSchedule()
        for(time in schedule){
            val subMap = mutableMapOf<String, Any>(Pair(name, true))
            map[time.toString()] = subMap
        }
        return map
    }

    // Save medicine to firebase
    fun save() {
        val medRef = getUserDBRef().child("medication/$name")
        val singleUpdate = mutableMapOf<String, Any>(
            "img" to img,
            "alarm/time" to startingTime,
            "alarm/frequency" to frequency
        )
        dependant?.let { singleUpdate["dependant"]  to dependant}
        medRef.updateChildren(singleUpdate)

        // Saving alarm references
        val alarmRef = getUserDBRef().child("alarms")
        alarmSchedule().forEach(fun(time: Int) {
            alarmRef.child("$time/$name").setValue(true)
        })
    }

    fun delete() {
        val medRef = getUserDBRef().child("medication/$name")
        medRef.removeValue()
        // Deleting alarm references
        val alarmRef = getUserDBRef().child("alarms")
        alarmSchedule().forEach(fun(time: Int) {
            alarmRef.child("$time/$name").removeValue()
        })
    }

    fun minutesToday(c: Calendar = Calendar.getInstance()) = c.get(Calendar.HOUR_OF_DAY)*60 + c.get(Calendar.MINUTE)

    fun minutesToAlarm() : Int {
        var minutesToAlarm = startingTime - minutesToday()
        if(minutesToAlarm < 0)
            minutesToAlarm += 24 * 60
        minutesToAlarm %= frequency
        return minutesToAlarm
    }

    fun nextAlarmTime() : Int {
        return minutesToAlarm() + minutesToday()
    }

    private fun alarmSchedule() : List<Int> {
        val schedule = mutableListOf<Int>()
        if(frequency < 10){ // DEBUG
            for (i in 0..10) {
                schedule.add(startingTime + i*frequency)
            }
        }
        else{
            var ini = startingTime%frequency
            while(ini < 24*60){
                schedule.add(ini)
                ini += frequency
            }
        }
        return schedule
    }

    fun setAlarm(context : Context, intent: Intent){
        var timeNow = SystemClock.elapsedRealtime()
        timeNow = (timeNow/60000)*60000 // Removing seconds from timeNow
        val minutesToday = minutesToday()
        alarmSchedule().forEach { time ->
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val pendingIntent = PendingIntent.getService(context, time, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { alarmIntent ->
                alarmIntent.putExtra("time", time)
                alarmIntent.flags = Intent.FLAG_RECEIVER_FOREGROUND // tentando resolver problema de atrasos na notificação
                PendingIntent.getBroadcast(context, time, alarmIntent, 0)
            }

            var alarmAt = timeNow + ((24*60 + time - minutesToday)%(24*60))*60*1000L
            alarmManager?.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    alarmAt,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
            )
            Log.d("ALARM", "Set alarm for $time triggering at $alarmAt")
        }
    }
}


fun medFromSnapshot(snap: DataSnapshot): Medication {
    val img : Int  = snap.child("img").value?.let { (it as Long).toInt() } ?: R.drawable.ic_broken_image
    val startingTime = snap.child("alarm/time").value as Long
    val frequency = snap.child("alarm/frequency").value as Long
    val dependant = snap.child("dependant").value as String?
    return Medication(
        snap.key!!,
        img,
        startingTime.toInt(),
        frequency.toInt(),
        dependant
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
            this.getInt("frequency"),
            this.getString("dependant")
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
        holder.name.text = med.name
        holder.img.setImageResource(med.img)
        return retView
    }

    internal class ViewHolder {
        lateinit var name: TextView
        lateinit var img: ImageView
    }
}