package com.example.kookcoching

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyPostActivity : AppCompatActivity() {

    lateinit var title : TextView
    lateinit var backBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypost)

        title = findViewById(R.id.post_title)
        backBtn = findViewById(R.id.back_btn)

        val intent = intent

        title.setText(intent.getStringExtra("name"))

        backBtn.setOnClickListener {
            finish()
        }


    }
}