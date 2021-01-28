package com.gafeol.dozeemdoze.models

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gafeol.dozeemdoze.R
import com.gafeol.dozeemdoze.util.formatTime
import com.gafeol.dozeemdoze.util.minutesToday
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.row_alarm.view.*

class Alarm(val time: Int,
                val medications: Array<String>) {

    constructor(snap: DataSnapshot) : this(
        snap.key!!.toInt(),
        snap.children.map { it.key!! }.toTypedArray()
    )

}

// Adapter of alarmList to an ArrayAdapter
class AlarmAdapter(context: Context, alarmList: List<Alarm>)
    : ArrayAdapter<Alarm>(context,  R.layout.row_alarm, alarmList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val alarm = getItem(position)!!
        var holder = ViewHolder()
        val retView: View

        if(convertView == null){
            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(R.layout.row_alarm, parent, false)
            holder.time = retView.timeTextView
            holder.meds = retView.medicationsTextView
            retView.tag = holder
        } else {
            retView = convertView
            holder = retView.tag as ViewHolder
        }
        holder.time.text = formatTime(alarm.time)
        holder.meds.text = alarm.medications.joinToString(", ")
        disableIfPast(retView, alarm.time)
        return retView
    }

    private fun disableIfPast(v: View, time : Int){
        Log.d("ALARM", "Time ${formatTime(time)} min $time minutes today ${minutesToday()}")
        if(time < minutesToday()) {
            Log.d("ALARM", "Time ${formatTime(time)} min $time  < ${minutesToday()}")
            val disabledColor = ContextCompat.getColor(v.context, R.color.text_disabled)
            v.timeTextView.setTextColor(disabledColor)
            v.medicationsTextView.setTextColor(disabledColor)
        }
        else{
            Log.d("ALARM", "Time ${formatTime(time)} min $time  >= ${minutesToday()}")
            val normalColor = ContextCompat.getColor(v.context, R.color.black)
            v.timeTextView.setTextColor(normalColor)
            v.medicationsTextView.setTextColor(normalColor)

        }
    }


    internal class ViewHolder {
        lateinit var time: TextView
        lateinit var meds: TextView
    }
}
