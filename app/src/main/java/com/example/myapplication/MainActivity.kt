package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val info: Button = findViewById(R.id.infoButton)
        val b3: Button = findViewById(R.id.button3)
        val b4: Button = findViewById(R.id.button4)
        val b5: Button = findViewById(R.id.button5)
        val fav: Button = findViewById(R.id.favorite)
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)



        b3.setOnClickListener{
            b3.startAnimation(animation)
            val intent = Intent(this, carActivity::class.java)
            startActivity(intent)
        }
        b4.setOnClickListener{
            b4.startAnimation(animation)
            val intent = Intent(this, partActivity::class.java)
            startActivity(intent)
        }
        b5.setOnClickListener{
            b5.startAnimation(animation)
            val intent = Intent(this, blockActivity::class.java)
            startActivity(intent)
        }
        info.setOnClickListener{
            info.startAnimation(animation)
            val intent = Intent(this, infoActivity::class.java)
            startActivity(intent)
        }
        fav.setOnClickListener{
            fav.startAnimation(animation)
            val intent = Intent(this, SavedList::class.java)
            startActivity(intent)
        }


    }

}