package com.example.kookcoching.Fragment.Share

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kookcoching.R
import java.text.SimpleDateFormat
import java.util.*

// 2020.10.28 / 문성찬 / PostViewActivity 작성
class PostViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_viewpost)
        val btn_return = findViewById(R.id.btn_returnToShare) as Button
        val btn_like = findViewById(R.id.btn_like) as Button
        val btn_scrap = findViewById(R.id.btn_scrap) as Button

        // 댓글 작성 버튼
        val btn_write = findViewById(R.id.btn_commentWrite) as Button

        val title = findViewById(R.id.tv_title) as TextView
        val content = findViewById(R.id.tv_content) as TextView
        val time = findViewById(R.id.tv_time) as TextView

        var inIntent: Intent = getIntent()
        title.setText(inIntent.getStringExtra("title"))
        content.setText(inIntent.getStringExtra("content"))

        // 2020.10.28 / 노용준 / epoch time to date
        val itemDate = Date(inIntent.getLongExtra("time",0))
        val dateFormat = SimpleDateFormat("MM/dd HH:mm")
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+09:00")
        val date = dateFormat.format(itemDate)
        time.setText(date)

        // "< 공유게시판" 버튼 클릭 시 동작
        btn_return.setOnClickListener {
            finish()
        }

    }
}