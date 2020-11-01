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
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.Fragment.Share.Post
import com.example.kookcoching.Fragment.Share.PostViewActivity
import com.example.kookcoching.Fragment.Share.WriteBoardActivity
import com.example.kookcoching.Fragment.Share.getPost
import com.example.kookcoching.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_share_board.*
import kotlinx.coroutines.*

class ShareBoardFragment : Fragment() {

    var firestore : FirebaseFirestore?= null
    var postList : ArrayList<getPost> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_share_board, container, false)
        val btn_move = view!!.findViewById(R.id.btn_moveToBoard) as Button
        val rv_post = view!!.findViewById(R.id.rv_post) as RecyclerView
        // Inflate the layout for this fragment

        btn_move.setOnClickListener {
            activity?.let {
                val intent = Intent(context, WriteBoardActivity::class.java)

                // 2020.10.27 / 노용준 / Chip의 개수를 파악하기 위함 (게시판 별로 개수가 다름을 구분)
                intent.putExtra("chip_type", "share")

                startActivityForResult(intent, 0)
            }
        }

        // 2020.10.26 / 노용준 / Get data in firestore
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val deferred : Deferred<ArrayList<getPost>> = async {
                var docRef = firestore!!.collection("share_post").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var title : String = document.get("title").toString()
                            var content : String = document.get("content").toString()
                            var time : Long = document.id.toLong()
                            var image : ArrayList<String> = document.get("image") as ArrayList<String>
                            var tag : String = document.get("tag").toString()
                            var post = getPost(title, content, time, image, tag);
                            postList.add(post)
                        }
                    }

                Thread.sleep(3000L)
                postList
            }

            val obj : ArrayList<getPost> = deferred.await();

            for ( i in obj) {
                Log.d(ContentValues.TAG, "obj : ${i.title} => ${i.content}")
            }


            // 2020.10.26 / 문성찬 / 리사이클뷰 어댑터 연결
            // runOnUiThread를 이용해서 코루틴에서도 UI 표시되게끔 설정
            activity?.runOnUiThread(Runnable {

                // 2020.11.01 / 문성찬 / tag명 검색 시 해당 tag 게시글 표시

                // 2020.11.01 / 문성찬 / tag별로 게시판 글 정렬 구현
                var sortedPostList : List<getPost> = obj.sortedBy { it.tag }
                var adapter = RecyclerAdapter(sortedPostList)

                // 2020.10.28 / 문성찬 / 리사이클뷰 클릭 시 인텐트로 액티비티 넘김
                adapter.setItemClickListener(object : RecyclerAdapter.itemClickListener{
                    override fun onClick(view: View, position: Int) {
                        // 넘어가는 인텐트 로그캣
                        Log.d("check","인덱스 : ${position}")
                        Log.d("check","title : ${obj[position].title}")
                        Log.d("check","content : ${obj[position].content}")

                        val intent = Intent(context, PostViewActivity::class.java)
                        intent.putExtra("title", obj[position].title)
                        intent.putExtra("content", obj[position].content)
                        intent.putExtra("time", obj[position].time)
                        intent.putExtra("image", obj[position].image)
                        startActivityForResult(intent, 0)
                    }
                })

                rv_post.adapter = adapter
                postList = arrayListOf()
            })
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }


}