package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.myapplication.DataClass.AutoPart
import com.google.firebase.firestore.FirebaseFirestore

class AutoPartDetailsActivity : AppCompatActivity() {
    private lateinit var autoPartImage: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var articleTextView: TextView
    private lateinit var blockTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var bodyTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_part_details)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)
        val copyButton = findViewById<ImageButton>(R.id.copyButton)
        autoPartImage = findViewById(R.id.image_d)
        nameTextView = findViewById(R.id.name_d)
        articleTextView = findViewById(R.id.article_d)
        blockTextView = findViewById(R.id.block_d)
        descriptionTextView = findViewById(R.id.description_d)
        bodyTextView = findViewById(R.id.body_d)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)

        val autoPart = intent.getParcelableExtra<AutoPart>("autopart")


        if (autoPart != null) {
            Glide.with(this)
                .load(autoPart.image)
                .into(autoPartImage)
        }

        if (autoPart != null) {
            nameTextView.text = autoPart.name
        }
        if (autoPart != null) {
            articleTextView.text = autoPart.article
        }
        if (autoPart != null) {
            blockTextView.text = autoPart.block
        }
        if (autoPart != null) {
            descriptionTextView.text = autoPart.description
        }

        val db = FirebaseFirestore.getInstance()
        val carsCollection = db.collection("cars")
        var bodyString = ""
        if (autoPart != null) {
            for (id in autoPart.body) {
                carsCollection.document(id).get().addOnSuccessListener { carDoc ->
                    val brand = carDoc.getString("brand")
                    val model = carDoc.getString("model")
                    val body = carDoc.getString("body")
                    if (brand != null && model != null && body != null) {
                        bodyString += "$brand $model $body\n"
                        bodyTextView.text = bodyString
                    }
                }
            }
        }
        saveButton.setOnClickListener {
            saveButton.startAnimation(animation)
            saveButton.isEnabled = false
            saveData()
        }
        backButton.setOnClickListener {
            backButton.startAnimation(animation)
            super.onBackPressed()
        }


        copyButton.setOnClickListener {
            copyButton.startAnimation(animation)
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", articleTextView.text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun saveData() {
        val saveButton = findViewById<Button>(R.id.saveButton)
        val autoPart = intent.getParcelableExtra<AutoPart>("autopart")
        if(autoPart != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Введите название")

            // Создание поля ввода
            val input = EditText(this)
            builder.setView(input)

            builder.setPositiveButton("ОК") {  dialog, which ->
                val key = input.text.toString()
                // Проверка, что ключ не пустой
                if (key.isNotBlank()) {
                    // Проверка, что ключ еще не существует в SharedPreferences
                    val sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE)
                    if (sharedPreferences.contains(key)) {
                        Toast.makeText(this, "Это название занято", Toast.LENGTH_SHORT).show()
                        saveButton.isEnabled = true
                    } else {
                        // Добавление нового ключа в SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString(key, autoPart.name+"split"+autoPart.article+"split"+autoPart.block+"split"+autoPart.description)
                        editor.apply()
                        Toast.makeText(this, "Запись сохранена в избранное", Toast.LENGTH_SHORT).show()
                        saveButton.isEnabled = true
                    }
                } else {
                    Toast.makeText(this, "Ошибка: название не может быть пустым", Toast.LENGTH_SHORT).show()
                    saveButton.isEnabled = true
                }
            }
            builder.setNegativeButton("Отмена", null)
            saveButton.isEnabled = true
            builder.show()
        }
    }
}
