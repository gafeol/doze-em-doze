package com.gafeol.dozeemdoze.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gafeol.dozeemdoze.R
import com.gafeol.dozeemdoze.util.getUserDBRef
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.row_medication.view.*

class Dependant(val name: String,
               val email: String?,
                val img: Int,
                val confirmation: Boolean) {

    constructor(snap: DataSnapshot) : this(
        snap.key!!,
        snap.child("email").value as String,
        (snap.child("img").value as Long).toInt(),
        (snap.child("confirmation").value as Boolean)
    )

    // Save dependant to firebase
    fun save() {
        val depRef = getUserDBRef().child("dependants/$name")
        var singleUpdate = mutableMapOf<String, Any>(
                "email" to (email ?: ""),
                "img" to img,
                "confirmation" to confirmation
        )
        depRef.setValue(singleUpdate)
        // TODO: propagate save on dependant`s uid tag as well, only if they have an email
    }

    fun delete() {
        val depRef = getUserDBRef().child("dependants/$name")
        depRef.removeValue()
    }
}

// Adapter of dependantList to an ArrayAdapter
class DependantAdapter(context: Context, dependantList: List<Dependant>)
    : ArrayAdapter<Dependant>(context,  R.layout.row_medication, dependantList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val med = getItem(position)!!
        var holder = ViewHolder()
        val retView: View

        if(convertView == null){
            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(R.layout.row_medication, parent, false)
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
