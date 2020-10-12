package com.example.kookcoching.Fragment.Information

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.kookcoching.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class WriteBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board)

        val btn_cancel = findViewById(R.id.btn_cancel) as Button
        val btn_store = findViewById(R.id.btn_store) as Button

        btn_cancel.setOnClickListener {
            finish()
        }

        btn_store.setOnClickListener {
            val title = findViewById(R.id.et_title) as EditText
            val content = findViewById<EditText>(R.id.et_content)
            var fbFirestore: FirebaseFirestore? = null
            fbFirestore = FirebaseFirestore.getInstance() // firestore 인스턴스 초기화
            fbFirestore?.collection("post")?.document(title.text.toString())?.set(Post(title.text.toString(),content.text.toString(), Timestamp.now())) // firestore 컬렉션에 저장하고자 하는 목록들을 저장
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}