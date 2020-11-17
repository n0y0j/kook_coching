package com.example.kookcoching

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.Fragment.Board.PostViewActivity
import com.example.kookcoching.Fragment.Board.getPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

// 2020.11.06 / 노용준 / 내가 좋아한 글, 내가 찜한 글, 내가 쓴 글
// Firestore에서 각 게시글 데이터를 가져온 후, 위 세개의 경우의 수에 맞는 게시글 목록을 생성한다.
class MyPostActivity : AppCompatActivity() {

    lateinit var title : TextView
    lateinit var backBtn : ImageButton
    var postList : ArrayList<getPost> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypost)

        title = findViewById(R.id.post_title)
        backBtn = findViewById(R.id.back_btn)

        val intent = intent
        val rv_post = findViewById(R.id.rv_my_post) as RecyclerView

        title.setText(intent.getStringExtra("name"))

        // 뒤로가기 버튼 클릭 시
        backBtn.setOnClickListener {
            finish()
        }

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val deferred : Deferred<ArrayList<QuerySnapshot?>> = async {
                var documentDB : ArrayList<QuerySnapshot?> = arrayListOf()
                // 각 게시판에서 데이터를 갖고옴
                var docRef = FirebaseFirestore.getInstance().collection("share_post").get()
                    .addOnSuccessListener { documents ->
                        documentDB.add(documents)
                    }.await()
                var docRef2 = FirebaseFirestore.getInstance().collection("major_post").get()
                    .addOnSuccessListener { documents ->
                        documentDB.add(documents)
                    }.await()
                var docRef3 = FirebaseFirestore.getInstance().collection("project_post").get()
                    .addOnSuccessListener { documents ->
                        documentDB.add(documents)
                    }.await()

                documentDB
            }

            val job : Job = async {

                val job_documents: ArrayList<QuerySnapshot?> = deferred.await()
                for (documents in job_documents) {

                    if (documents != null) {
                        for (document in documents) {
                            var nickname: String = document.get("nickname").toString()
                            var author: String = document.get("author").toString()
                            var title: String = document.get("title").toString()
                            var content: String = document.get("content").toString()
                            var time: Long = document.id.toLong()
                            var image: ArrayList<String> =
                                document.get("image") as ArrayList<String>
                            var tag: String = document.get("tag").toString()
                            var good: ArrayList<String> =
                                document.get("goodCount") as ArrayList<String>
                            var scrap: ArrayList<String> =
                                document.get("scrapCount") as ArrayList<String>

                            // 경우의 수에 따라 게시글 목록을 설정
                            when (intent.getStringExtra("kind")) {
                                // 좋아요
                                "goodCount" -> {
                                    if (good.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                                        var post = getPost(
                                            title,
                                            content,
                                            time,
                                            image,
                                            tag,
                                            author,
                                            nickname,
                                            good,
                                            scrap
                                        )
                                        postList.add(post)
                                    }
                                }
                                "scrapCount" -> {
                                    // 스크랩
                                    if (scrap.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                                        var post = getPost(
                                            title,
                                            content,
                                            time,
                                            image,
                                            tag,
                                            author,
                                            nickname,
                                            good,
                                            scrap
                                        )
                                        postList.add(post)
                                    }
                                }
                                "write" -> {
                                    // 작성한 글
                                    if (author.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                                        var post = getPost(
                                            title,
                                            content,
                                            time,
                                            image,
                                            tag,
                                            author,
                                            nickname,
                                            good,
                                            scrap
                                        )
                                        postList.add(post)
                                    }
                                }
                            }

                        }
                    }
                }
            }

            job.join()

            runOnUiThread {
                var adapter = RecyclerAdapter(postList)
                Log.d("zzzzzzzzzzzzzzzz", postList.toString())

                adapter.setItemClickListener(object : RecyclerAdapter.itemClickListener{
                    override fun onClick(view: View, position: Int) {
                        // 넘어가는 인텐트 로그캣
                        Log.d("check","인덱스 : ${position}")
                        Log.d("check","title : ${postList[position].title}")
                        Log.d("check","content : ${postList[position].content}")

                        val intent = Intent(applicationContext, PostViewActivity::class.java)
                        intent.putExtra("title", postList[position].title)
                        intent.putExtra("content", postList[position].content)
                        intent.putExtra("time", postList[position].time)
                        intent.putExtra("image", postList[position].image)
                        intent.putExtra("author", postList[position].author)
                        intent.putExtra("nickname", postList[position].nickname)
                        intent.putExtra("goodCount", postList[position].goodCount)
                        intent.putExtra("scrapCount", postList[position].scrapCount)

                        startActivityForResult(intent, 0)
                    }
                }
                )
                rv_post.adapter = adapter
            }

        }
        }


    }