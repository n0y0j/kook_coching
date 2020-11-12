package com.example.kookcoching

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    lateinit var backButton : ImageButton
    lateinit var myWrite : LinearLayout
    lateinit var myScrap : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)

        backButton = findViewById(R.id.back_btn)
        myWrite = findViewById(R.id.my_write_layout)
        myScrap = findViewById(R.id.my_scrap_layout)

        backButton.setOnClickListener {
            finish()
        }
        
        myWrite.setOnClickListener {
            val intent = Intent(this, MyPostActivity::class.java)
            intent.putExtra("name", "내가 쓴 글")
            startActivity(intent)
        }
        
        myScrap.setOnClickListener {
            val intent = Intent(this, MyPostActivity::class.java)
            intent.putExtra("name", "내가 찜한 글")
            startActivity(intent)
        }



    }
}