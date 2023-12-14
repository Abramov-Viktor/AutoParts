package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.myapplication.DataClass.AutoPart
import com.example.myapplication.DataClass.Car
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.ibm.icu.text.Transliterator
import org.apache.commons.codec.language.Soundex

class carActivity : AppCompatActivity()  {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car)

        db = FirebaseFirestore.getInstance()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val brandEditText = findViewById<EditText>(R.id.brand_et)
        val modelEditText = findViewById<EditText>(R.id.model_et)
        val yearEditText = findViewById<EditText>(R.id.year_et)
        val bodyEditText = findViewById<EditText>(R.id.body_et)

        val blockSpinner = findViewById<Spinner>(R.id.spinner)
        val searchButton = findViewById<Button>(R.id.search_car)
        val backButton = findViewById<Button>(R.id.button)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)

        val items = arrayOf("Не уточнять", "ДВС", "ходовая часть", "электрика", "тормозная система")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        blockSpinner.adapter = adapter


        fun searchAutoParts(
            brand: String,
            model: String,
            year: String?,
            body: String?,
            block: String?

        ) {
            var query = db.collection("cars")
            query.get()
                .addOnSuccessListener { carsSnapshot ->
                    val carIds = mutableListOf<String>()
                    try{
                        val soundex = Soundex()
                        val fromRus = Transliterator.getInstance("Cyrillic-Latin")
                        val stbrand = soundex.encode(fromRus.transliterate(brand))
                        val stmodel = soundex.encode(fromRus.transliterate(model))
                        val stbody = fromRus.transliterate(body)
                    for (carDoc in carsSnapshot) {

                        val car = carDoc.toObject(Car::class.java)
                        val sbrand = soundex.encode(carDoc.get("brand").toString())
                        val smodel = soundex.encode(carDoc.get("model").toString())

                        if(sbrand.substring(0, 4) == stbrand.substring(0, 4)) {
                            if(smodel.substring(0, 4) == stmodel.substring(0, 4)) {
                                if (year != null && year.isNotBlank()) {
                                    if (car.year.contains(year)) {
                                        if (body != null && body.isNotBlank()) {
                                            if (car.body.lowercase(Locale.getDefault())
                                                    .contains(stbody.lowercase(Locale.getDefault()))
                                            )
                                                carIds.add(carDoc.id)
                                        } else carIds.add(carDoc.id)
                                    }
                                } else if (body != null && body.isNotBlank()) {
                                    if (car.body.lowercase(Locale.getDefault())
                                            .contains(body.lowercase(Locale.getDefault()))
                                    )
                                        carIds.add(carDoc.id)
                                } else carIds.add(carDoc.id)
                            }
                        }


                    }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Попробуйте ввести на латинице", Toast.LENGTH_SHORT).show()
                    }
                    db.collection("autoparts")
                        .let { if (block != null) it.whereEqualTo("block", block) else it }
                        .get()
                        .addOnSuccessListener { autopartsSnapshot ->
                            val autoParts = mutableListOf<AutoPart>()
                            for (autoPartDoc in autopartsSnapshot) {
                                val autoPart = autoPartDoc.toObject(AutoPart::class.java)
                                if (autoPart.body.any { carIds.contains(it) }) {
                                    autoParts.add(autoPart)
                                }
                            }
                            if (autoParts.isNotEmpty()) {
                                val intent = Intent(this, AutoPartsActivity::class.java)
                                intent.putExtra("autoparts", autoParts.toTypedArray())
                                startActivity(intent)
                                progressBar.visibility = View.GONE
                                searchButton.isEnabled = true
                            } else {
                                Toast.makeText(
                                    this,
                                    "Запчасти на этот автомобиль ещё не добавлены",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar.visibility = View.GONE
                                searchButton.isEnabled = true
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка при поиске данных: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }


        searchButton.setOnClickListener {
            searchButton.startAnimation(animation)
            searchButton.isEnabled = false

            progressBar.visibility = View.VISIBLE
            val brand = brandEditText.text.toString().filterNot { it.isWhitespace() }.lowercase(Locale.getDefault())
            val model = modelEditText.text.toString().filterNot { it.isWhitespace() }.lowercase(Locale.getDefault())
            val year = yearEditText.text.toString().filterNot { it.isWhitespace() }
            val body = bodyEditText.text.toString().filterNot { it.isWhitespace() }
            val block = if (blockSpinner.selectedItemPosition > 0) {
                blockSpinner.selectedItem.toString()
            } else {
                null
            }
            searchAutoParts(brand, model, year, body, block)

        }
        backButton.setOnClickListener {
            backButton.startAnimation(animation)
            super.onBackPressed()
        }

    }

}