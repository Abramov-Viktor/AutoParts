package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class SavedDataActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var articleTextView: TextView
    private lateinit var blockTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var back: Button
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_data)

        // Инициализация элементов TextView
        nameTextView = findViewById(R.id.name_s)
        articleTextView = findViewById(R.id.article_s)
        blockTextView = findViewById(R.id.block_s)
        descriptionTextView = findViewById(R.id.description_s)
        back = findViewById(R.id.saveButton)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)

        val selectedKey = intent.getStringExtra("selectedKey")
        supportActionBar?.apply {
            title = selectedKey
        }
        val sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE)
        val str = sharedPreferences.getString(selectedKey, "")?.split("split")

        nameTextView.text = str!![0]
        articleTextView.text = str[1]
        blockTextView.text = str[2]
        descriptionTextView.text = str[3]


        back.setOnClickListener {
            back.startAnimation(animation)
            super.onBackPressed()
        }
    }
}