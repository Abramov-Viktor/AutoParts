package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class blockActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)

        // Инициализируем базу данных
        database = FirebaseDatabase.getInstance().reference

        // Находим наши элементы интерфейса по ID
        val requestEditText = findViewById<EditText>(R.id.requestMultiLine)
        val contactEditText = findViewById<EditText>(R.id.contactMultiLine)
        val sendButton = findViewById<Button>(R.id.button7)
        val backButton = findViewById<Button>(R.id.button6)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarS)


        // Устанавливаем обработчик нажатия на кнопку
        sendButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            sendButton.isEnabled = false
            // Получаем текст из полей ввода
            val requestText = requestEditText.text.toString().trim()
            val contactText = contactEditText.text.toString().trim()

            // Проверяем, что поля не пустые
            if (requestText.isEmpty() || contactText.isEmpty()) {
                // Если есть пустые поля, выводим сообщение об ошибке
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                sendButton.isEnabled = true
                return@setOnClickListener
            }

            // Получаем IMEI устройства
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            // Получаем текущую дату и время
            val currentDate = getCurrentDate()

            // Проверяем, не превысил ли пользователь лимит записей
            database.child(deviceId).child(currentDate).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.childrenCount >= 5) {
                        // Если лимит записей превышен, выводим сообщение и не записываем данные в базу
                        Toast.makeText(this@blockActivity, "Достигнут лимит записей для этого устройства на сегодня", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                        sendButton.isEnabled = true
                    } else {
                        // Если лимит записей не превышен, записываем данные в базу данных
                        val key = database.child(deviceId).child(currentDate).push().key ?: ""
                        val data = hashMapOf(
                            "request" to requestText,
                            "contact" to contactText,
                            "timestamp" to SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        )

                        database.child(deviceId).child(currentDate).child(key).setValue(data)
                        Toast.makeText(this@blockActivity, "Ваш запрос отправлен", Toast.LENGTH_SHORT).show()
                        requestEditText.text.clear()
                        contactEditText.text.clear()
                        progressBar.visibility = View.GONE
                        sendButton.isEnabled = true
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("blockActivity", "onCancelled", databaseError.toException())
                }
            })
        }
        backButton.setOnClickListener {
            super.onBackPressed();
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(Date())
    }
}