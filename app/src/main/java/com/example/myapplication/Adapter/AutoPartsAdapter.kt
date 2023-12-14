package com.example.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.myapplication.DataClass.AutoPart
import com.example.myapplication.R
class AutoPartsAdapter(private val parts: Array<AutoPart>) : BaseAdapter() {
//переопределенный базовый адаптер
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.auto_part_item, parent, false)
        val part = getItem(position)

        view.findViewById<TextView>(R.id.nameTextView).text = part.name
        view.findViewById<TextView>(R.id.articleTextView).text = part.article
        Glide.with(view.context).load(part.image).into(view.findViewById(R.id.partImageView))

        return view
    }

    override fun getItem(position: Int): AutoPart {
        return parts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return parts.size
    }
}
