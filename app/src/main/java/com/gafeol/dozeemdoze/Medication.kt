package com.gafeol.dozeemdoze

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class Medication(val name: String, val img: Int) {

    fun bundle() : Bundle {
        var bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("img", img)
        return bundle
    }
}

fun Bundle.unbundleMedication() : Medication {
    return Medication(this.getString("name")!!, this.getInt("img")!!)
}

class MedicationAdapter(context: Context, medList: List<Medication>)
    : ArrayAdapter<Medication>(context,  R.layout.row_medication, medList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val med = getItem(position)!!
        var holder = ViewHolder()
        var retView: View

        if(convertView == null){
            val inflater = LayoutInflater.from(context);
            retView = inflater.inflate(R.layout.row_medication, parent, false);
            holder.name = retView.findViewById(R.id.medTitleTextView)
            holder.img = retView.findViewById(R.id.medImageView)
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

