package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.text.Typography.section

class infoActivity : AppCompatActivity() {
    private lateinit var section1: TextView
    private lateinit var section2: TextView
    private lateinit var section3: TextView
    private lateinit var sec1: ImageButton
    private lateinit var sec2: ImageButton
    private lateinit var sec3: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)
        // Находим элементы по их id
        section1 = findViewById(R.id.section1_text)
        section2 = findViewById(R.id.section2_text)
        section3 = findViewById(R.id.section3_text)
        sec1 = findViewById(R.id.section1_expand)
        sec2 = findViewById(R.id.section2_expand)
        sec3 = findViewById(R.id.section3_expand)

        // Устанавливаем слушателей клика для каждой секции
        sec1.setOnClickListener { toggleSection(section1) }
        sec2.setOnClickListener { toggleSection(section2) }
        sec3.setOnClickListener { toggleSection(section3) }
        val backButton = findViewById<Button>(R.id.button9)
        backButton.setOnClickListener {
            backButton.startAnimation(animation)
            super.onBackPressed();
        }

    }

    private fun toggleSection(section: TextView) {
        // Получаем видимость текста секции
        val textVisibility = section.visibility

        // Устанавливаем видимость текста в зависимости от его текущего состояния
        section.visibility = if (textVisibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}