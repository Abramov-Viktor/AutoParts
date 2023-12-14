package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog

class SavedList : AppCompatActivity() {
    private lateinit var del: Button
    private lateinit var back: Button
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_list)
        del = findViewById(R.id.clearButton)
        back = findViewById(R.id.backButton)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)

        val sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE)
        val sharedPreferencesMap = sharedPreferences.all
        val sharedPreferencesNames = sharedPreferencesMap.keys.toTypedArray()

        val listView = findViewById<ListView>(R.id.saved_parts_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sharedPreferencesNames)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedKey = listView.getItemAtPosition(position) as String
            val intent = Intent(this, SavedDataActivity::class.java)
            intent.putExtra("selectedKey", selectedKey)
            startActivity(intent)
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedKey = parent.getItemAtPosition(position) as String
            showDeleteDialog(selectedKey)
            true
        }
        del.setOnClickListener {
            del.startAnimation(animation)
            del.isEnabled = false
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Вы действительно хотите очистить избранное?")

            builder.setPositiveButton("Удалить все") { dialog, which ->
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                super.onBackPressed()
                del.isEnabled = true
                Toast.makeText(this, "Список очищен", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Отмена", null)
            del.isEnabled = true
            builder.show()
        }
        back.setOnClickListener {
            back.startAnimation(animation)
            super.onBackPressed()
        }
    }
    private fun refreshListView() {
        val sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE)
        val sharedPreferencesMap = sharedPreferences.all
        val sharedPreferencesNames = sharedPreferencesMap.keys.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sharedPreferencesNames)
        val listView = findViewById<ListView>(R.id.saved_parts_list)
        listView.adapter = adapter
    }
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun showDeleteDialog(key: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Удаление")
            .setMessage("Вы действительно хотите убрать эту запись из избранного?")
            .setPositiveButton("Удалить") { dialog, which ->
                val sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove(key)
                editor.apply()
                refreshListView()

                Toast.makeText(this, "Запись удалена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
