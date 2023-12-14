package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.myapplication.Adapter.AutoPartsAdapter
import com.example.myapplication.DataClass.AutoPart
import com.google.firebase.firestore.FirebaseFirestore

class AutoPartsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val autoParts by lazy {
        intent.getParcelableArrayExtra("autoparts")?.mapNotNull { it as? AutoPart }?.toTypedArray()
    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auto_parts)
        val backButton = findViewById<Button>(R.id.button8)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)
        listView = findViewById(R.id.auto_parts_list)
        listView.adapter = autoParts?.let { AutoPartsAdapter(it) }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPart = autoParts?.get(position)
            val intent = Intent(this, AutoPartDetailsActivity::class.java)
            intent.putExtra("autopart", selectedPart)
            startActivity(intent)
        }
        backButton.setOnClickListener {
            backButton.startAnimation(animation)
            super.onBackPressed();
        }

    }


    companion object {
        private const val TAG = "AutoPartsActivity"
    }
}

