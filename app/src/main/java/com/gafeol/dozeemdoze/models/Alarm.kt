package com.gafeol.dozeemdoze.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.gafeol.dozeemdoze.R
import com.gafeol.dozeemdoze.util.formatTime
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
        return retView
    }

    internal class ViewHolder {
        lateinit var time: TextView
        lateinit var meds: TextView
    }
}
