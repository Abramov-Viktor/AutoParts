package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.myapplication.DataClass.AutoPart
import com.example.myapplication.DataClass.Car
import com.google.firebase.firestore.FirebaseFirestore
import com.ibm.icu.text.Transliterator
import org.apache.commons.codec.language.Soundex
import java.util.*

@Suppress("NAME_SHADOWING")
class partActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_part)
        val progressBarA = findViewById<ProgressBar>(R.id.progressBarA)
        val progressBarB = findViewById<ProgressBar>(R.id.progressBarB)
        // находим элементы интерфейса по id
        val editText1 = findViewById<EditText>(R.id.searchArticle)
        val editText2 = findViewById<EditText>(R.id.searchName)
        val button1 = findViewById<Button>(R.id.search1)
        val button2 = findViewById<Button>(R.id.search2)
        val backButton = findViewById<Button>(R.id.button10)
        val switch = findViewById<Switch>(R.id.switch1)
        val attention = findViewById<TextView>(R.id.textView12)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                attention.visibility = View.VISIBLE
            } else attention.visibility = View.GONE

        }

        button1.setOnClickListener {
            button1.startAnimation(animation)
            progressBarA.visibility = View.VISIBLE
            button1.isEnabled = false
            val article = editText1.text.toString().filterNot { it.isWhitespace() }
            searchByArticle(article)
        }

        button2.setOnClickListener {
            button2.startAnimation(animation)
            progressBarB.visibility = View.VISIBLE
            button2.isEnabled = false
            val name = editText2.text.toString().trim()
            if (switch.isChecked) {
                searchByFilter(name)
            } else searchByName(name)
        }
        backButton.setOnClickListener {
            backButton.startAnimation(animation)
            super.onBackPressed();
        }
    }

    private fun searchByArticle(article: String) {
        val progressBarA = findViewById<ProgressBar>(R.id.progressBarA)
        val button1 = findViewById<Button>(R.id.search1)
        db.collection("autoparts")

            .get()
            .addOnSuccessListener { result ->

                val parts = mutableListOf<AutoPart>()
                for (document in result) {
                    val part = document.toObject(AutoPart::class.java)
                    if (part.article.lowercase(Locale.getDefault()) == article.lowercase(Locale.getDefault())) {
                        parts.add(part)
                    }
                }

                if (parts.isNotEmpty()) {
                    val intent = Intent(this, AutoPartsActivity::class.java)
                    intent.putExtra("autoparts", parts.toTypedArray())
                    startActivity(intent)
                    progressBarA.visibility = View.GONE
                    button1.isEnabled = true
                } else {
                    Toast.makeText(
                        this,
                        "Ни одна деталь не соответствует введённому артикулу",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarA.visibility = View.GONE
                    button1.isEnabled = true
                }
            }

    }

    // функция для поиска документов по полю name
    private fun searchByName(name: String) {
        val progressBarB = findViewById<ProgressBar>(R.id.progressBarB)
        val button2 = findViewById<Button>(R.id.search2)
        db.collection("autoparts")
            .get()
            .addOnSuccessListener { result ->
                val parts = mutableListOf<AutoPart>()
                for (document in result) {
                    val part = document.toObject(AutoPart::class.java)
                    val strDB = part.name.lowercase(Locale.getDefault()).split(" ")
                    val strUse = name.lowercase(Locale.getDefault()).split(" ")
                    val result = strUse
                        .any { it in strDB }
                    if (result) {
                        parts.add(part)
                    }

                }

                if (parts.isNotEmpty()) {
                    val intent = Intent(this, AutoPartsActivity::class.java)
                    intent.putExtra("autoparts", parts.toTypedArray())
                    startActivity(intent)
                    progressBarB.visibility = View.GONE
                    button2.isEnabled = true
                } else {
                    Toast.makeText(
                        this,
                        "К сожалению, мы ещё не добавили эту автозапчасть, можете оставить запрос на добавление.",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBarB.visibility = View.GONE
                    button2.isEnabled = true
                }
            }
    }
    private fun searchByFilter(name: String) {
        val progressBarB = findViewById<ProgressBar>(R.id.progressBarB)
        val button2 = findViewById<Button>(R.id.search2)
        val parts = mutableListOf<AutoPart>()
        val cars = mutableListOf<String>()
        db.collection("cars")
            .get()
            .addOnSuccessListener { carResult ->
                try {
                    val soundex = Soundex()
                    val fromRus = Transliterator.getInstance("Russian-Latin/BGN")
                    val stname = fromRus.transliterate(name).lowercase()
                    val stnameUse = stname.split(" ")
                    for (carDoc in carResult) {
                        val sbrand = carDoc.get("brand").toString().lowercase()
                        val smodel = carDoc.get("model").toString().lowercase()

                        for (word in stnameUse){
                            if(soundex.encode(sbrand).substring(0, 4).contains(soundex.encode(word).substring(0, 4))
                                || soundex.encode(smodel).substring(0, 4).contains(soundex.encode(word).substring(0, 4))){
                                cars.add(carDoc.id)
                                break
                            }
                        }

                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Попробуйте ввести автомобиль на латинице", Toast.LENGTH_SHORT).show()
                }

                db.collection("autoparts")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val part = document.toObject(AutoPart::class.java)
                            val strDB = part.name.lowercase(Locale.getDefault()).split(" ")
                            val strUse = name.lowercase(Locale.getDefault()).split(" ")
                            val result = strUse.any { it in strDB }
                            if (result) {
                                val hasMatchingCar = part.body.any { cars.contains(it) }
                                if (hasMatchingCar) {
                                    parts.add(part)
                                }
                            }
                        }

                        if (parts.isNotEmpty()) {
                            val intent = Intent(this, AutoPartsActivity::class.java)
                            intent.putExtra("autoparts", parts.toTypedArray())
                            startActivity(intent)
                            progressBarB.visibility = View.GONE
                        } else {
                            Toast.makeText(
                                this,
                                "Результатов нет, попробуйте без учёта автомобиля",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBarB.visibility = View.GONE
                        }
                        button2.isEnabled = true
                    }
            }


    }

}