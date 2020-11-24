package com.gafeol.dozeemdoze

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class Medicacao(val name: String, val img: Long) { }

class MedicacaoAdapter(context: Context, medList: List<Medicacao>) : ArrayAdapter<Medicacao>(context,  R.layout.row_medicacao, medList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val med = getItem(position)!!
        var holder = ViewHolder()
        var retView: View

        if(convertView == null){
            val inflater = LayoutInflater.from(context);
            retView = inflater.inflate(R.layout.row_medicacao, parent, false);
            holder.name = retView.findViewById(R.id.medTitleTextView)
            holder.img = retView.findViewById(R.id.medImageView)
            retView.tag = holder
        } else {
            retView = convertView
            holder = retView.tag as ViewHolder
        }
        holder.name.setText(med.name)
        holder.img.setImageResource(R.drawable.ic_pills)
        return retView
    }

    internal class ViewHolder {
        lateinit var name: TextView
        lateinit var img: ImageView
    }
}

