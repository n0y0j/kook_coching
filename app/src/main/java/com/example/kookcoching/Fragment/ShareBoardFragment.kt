package com.example.kookcoching.Fragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.Fragment.Share.Post
import com.example.kookcoching.Fragment.Share.WriteBoardActivity
import com.example.kookcoching.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_shard_board.*
import kotlinx.coroutines.*

class ShareBoardFragment : Fragment() {

    var firestore : FirebaseFirestore?= null
    var postList : ArrayList<Post> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_shard_board, container, false)
        val btn_move = view!!.findViewById(R.id.btn_moveToBoard) as Button
        val rv_post = view!!.findViewById(R.id.rv_post) as RecyclerView
        // Inflate the layout for this fragment

        btn_move.setOnClickListener {
            activity?.let {
                val intent = Intent(context, WriteBoardActivity::class.java)
                startActivity(intent)
            }
        }

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val deferred : Deferred<ArrayList<Post>> = async {
                var docRef = firestore!!.collection("post").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var title : String = document.get("title").toString();
                            var content : String = document.get("content").toString();
                            var post = Post(title, content, Timestamp.now());
                            postList.add(post)
                        }
                    }

                Thread.sleep(3000L)
                postList
            }

            val obj : ArrayList<Post> = deferred.await();

            for ( i in obj) {
                Log.d(ContentValues.TAG, " ${i.title} => ${i.content}")
            }

            // runOnUiThread를 이용해서 코루틴에서도 UI 표시되게끔 설정
            activity?.runOnUiThread(Runnable {

                val adapter = RecyclerAdapter(postList)
                rv_post.adapter = adapter
            })

        }

        // 리사이클뷰 어댑터 연결 -> 이거를 코루틴 안으로 넣어야함
        // 게시글 작성 저장 후에 표시는 됨


        return view
    }

}