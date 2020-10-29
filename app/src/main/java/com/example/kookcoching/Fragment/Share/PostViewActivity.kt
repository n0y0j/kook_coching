package com.example.kookcoching.Fragment.Share

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kookcoching.Adapter.CommentRecyclerAdapter
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.share_viewpost.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// 2020.10.28 / 문성찬 / PostViewActivity 작성
class PostViewActivity : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    var commentList = arrayListOf<getComment>() // 댓글을 받아올 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_viewpost)
        firestore = FirebaseFirestore.getInstance()

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
        val itemDate = Date(inIntent.getLongExtra("time", 0))
        val dateFormat = SimpleDateFormat("MM/dd HH:mm")
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+09:00")
        val date = dateFormat.format(itemDate)
        time.setText(date)


        // 2020.10.30 / 노성환 / firestore 하위 컬렉션인 comment의 data 가져오기
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val deferred: Deferred<ArrayList<getComment>> = async {
                var docRef = firestore!!.collection("share_post")
                    .document(inIntent.getLongExtra("time", 0).toString())
                    .collection("share_post_comment").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var comment: String = document.get("comment").toString()
                            var time: Long = document.id.toLong()
                            var write_comment = getComment(comment, time)
                            commentList.add(write_comment)
                        }
                    }
                Thread.sleep(3000L)
                commentList
            }
            val obj: ArrayList<getComment> = deferred.await()

            for (i in obj) {
                Log.d(ContentValues.TAG, "obj : ${i.comment}, ${i.time}")
            }

            this@PostViewActivity.runOnUiThread(Runnable {

                // 리사이클러뷰에 연결
                val adapter = CommentRecyclerAdapter(commentList)
                rv_comment.adapter = adapter

                val layoutManager = LinearLayoutManager(this@PostViewActivity)
                rv_comment.layoutManager = layoutManager
                rv_comment.setHasFixedSize(true)
            })
        }


        // "< 공유게시판" 버튼 클릭 시 동작
        btn_return.setOnClickListener {
            finish()
        }

        // 2020.10.28 / 노성환 / 댓글 작성 버튼 누르면 firestore의 하위 컬렉션에 저장
        btn_write.setOnClickListener {
            val comment = findViewById<EditText>(R.id.et_comment)
            var fbFirebase: FirebaseFirestore? = null

            fbFirebase = FirebaseFirestore.getInstance()
            fbFirebase?.collection("share_post")
                ?.document(inIntent.getLongExtra("time", 0).toString())
                // 2020.10.29 / 노용준 / document name을 epoch time으로 설정 (시간 순으로 자동정렬)
                .collection("share_post_comment").document(System.currentTimeMillis().toString())
                ?.set(Comment(comment.text.toString()))
            comment.setText("")
            Toast.makeText(this, "댓글이 입력되었습니다.", Toast.LENGTH_SHORT).show()
            // 2010.10.30 / 노성환 / 댓글 입력 후 해당 게시물 새로고침
            finish()
            startActivity(getIntent())
        }

    }
}