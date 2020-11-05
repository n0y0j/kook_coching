package com.example.kookcoching.Fragment.Share

import androidx.appcompat.widget.Toolbar
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kookcoching.Adapter.CommentRecyclerAdapter
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.Fragment.ShareBoardFragment
import com.example.kookcoching.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.share_viewpost.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// 2020.10.28 / 문성찬 / PostViewActivity 작성
class PostViewActivity : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    var commentList = arrayListOf<getComment>() // 댓글을 받아올 리스트
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_viewpost)

        // 2020.10.30 / 노성환 / 툴바에 메뉴버튼을 누르기 위함
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
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

        // 2020.10.30 / 노용준 / image의 개수만큼 View 동적 생성
        val imageList = inIntent.getStringArrayListExtra("image")

        val image = findViewById(R.id.image_group) as LinearLayout
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        if (imageList != null) {
            for (item in imageList) {
                var temp: ImageView = ImageView(this)
                temp.layoutParams = layoutParams
                // url을 통해 이미지를 가져오는 Glide 라이브러리 사용
                Glide.with(this).load(item).into(temp)

                image.addView(temp)
            }
        }

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

    // 2020.10.30 / 노성환 / 툴바에 메뉴 추가 + 해당 작성자만 편집가능하게 함(2020.11.05)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        firebaseAuth = FirebaseAuth.getInstance()
        var author = intent.getStringExtra("author")
        Log.d("AUTHOR", author)
        // 현재 접속한 uid와 게시물의 uid가 같은지 확인하고, 같을 때만 메뉴가 나올 수 있도록 구현
        if (author.equals(firebaseAuth.currentUser?.uid.toString())) {
            menuInflater.inflate(R.menu.option_menu, menu)
        }
        return true
    }

    // 2020.10.31 / 노성환 / 툴바에 추가한 메뉴 기능 구현
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var inIntent: Intent = getIntent()
        var before_title = inIntent.getStringExtra("title")
        var before_content = inIntent.getStringExtra("content")
        var before_time = inIntent.getLongExtra("time", 0)
        var db = FirebaseFirestore.getInstance()

        when (item?.itemId) {
            // 게시글 삭제
            R.id.delete_post -> {
                // firestore에서 게시물은 삭제되었지만, 댓글은 여전히 남아있어 댓글먼저 다 지움
                db.collection("share_post").document(inIntent.getLongExtra("time", 0).toString())
                    .collection("share_post_comment")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                db.collection("share_post")
                                    .document(inIntent.getLongExtra("time", 0).toString())
                                    .collection("share_post_comment").document(document.id)
                                    .delete()
                            }
                        }
                    }
                Thread.sleep(1000L)
                // 그 이후 게시글을 지움
                firestore!!.collection("share_post")
                    .document(inIntent.getLongExtra("time", 0).toString()).delete()
                    .addOnCompleteListener {
                        // 지우면 다시 게시판 목록으로 이동
                        finish()
                    }
            }
            // 게시글 수정
            R.id.remake_post -> {
                val intent = Intent(this, WriteBoardActivity::class.java)
                // 기존의 제목, 내용 전달
                intent.putExtra("before_title", before_title)
                intent.putExtra("before_content", before_content)
                intent.putExtra("before_time", before_time)
                intent.putExtra("chip_type", "share")
                intent.putExtra("check", "update")
                startActivityForResult(intent, 0)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}