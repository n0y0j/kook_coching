package com.example.kookcoching.Fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.Fragment.Board.PostViewActivity
import com.example.kookcoching.Fragment.Board.WriteBoardActivity
import com.example.kookcoching.Fragment.Board.getPost
import com.example.kookcoching.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

// 2020.11.14 / 문성찬 / 프로젝트 게시판 프래그먼트 구현
class ProjectFragment : Fragment() {

    var firestore : FirebaseFirestore?= null
    var postList : ArrayList<getPost> = arrayListOf()
    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_project, container, false)
        val btn_move = view!!.findViewById(R.id.btn_moveToBoard) as Button
        val rv_post = view!!.findViewById(R.id.rv_post) as RecyclerView
        val btn_search = view!!.findViewById(R.id.btn_search) as Button
        val et_search = view!!.findViewById(R.id.et_search) as EditText
        val spinner = view!!.findViewById(R.id.project_spinner) as Spinner
        // Inflate the layout for this fragment

        // 2020.11.02 / 문성찬 / spinner 어댑터 설정
        var selectedTag : String = ""
        val items = resources.getStringArray(R.array.project_board)
        spinner.adapter = ArrayAdapter(
            activity as Context,
            R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.project_board)
        )

        btn_move.setOnClickListener {
            activity?.let {
                val intent = Intent(context, WriteBoardActivity::class.java)

                // 2020.10.27 / 노용준 / Chip의 개수를 파악하기 위함 (게시판 별로 개수가 다름을 구분)
                intent.putExtra("chip_type", "project")
                intent.putExtra("check", "new")

                startActivityForResult(intent, 0)
            }
        }

        // 2020.10.26 / 노용준 / Get data in firestore
        // return을 요구하는 deffered와 return을 요구하지 않는 job을 사용하여 동기/비동기 작업
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val deferred : Deferred<QuerySnapshot?> = async {
                var documentDB : QuerySnapshot? = null
                var docRef = firestore!!.collection("project_post").get()
                    .addOnSuccessListener { documents ->
                        documentDB = documents
                    }.await() // Task.await()

                documentDB
            }

            val job : Job = async {

                val documents : QuerySnapshot? = deferred.await()

                if (documents != null) {
                    for (document in documents) {
                        var nickname: String = document.get("nickname").toString()
                        var author: String = document.get("author").toString()
                        var title : String = document.get("title").toString()
                        var content : String = document.get("content").toString()
                        var time : Long = document.id.toLong()
                        var image : ArrayList<String> = document.get("image") as ArrayList<String>
                        var tag : String = document.get("tag").toString()
                        var good : ArrayList<String> = document.get("goodCount") as ArrayList<String>
                        var scrap : ArrayList<String> = document.get("scrapCount") as ArrayList<String>
                        var post = getPost(title, content, time, image, tag, author, nickname, good, scrap);
                        postList.add(post)
                    }
                }
            }

            job.join()

            for ( i in postList) {
                Log.d(ContentValues.TAG, "obj : ${i.title} => ${i.content}")
            }

            // 2020.10.26 / 문성찬 / 리사이클뷰 어댑터 연결
            // runOnUiThread를 이용해서 코루틴에서도 UI 표시되게끔 설정
            activity?.runOnUiThread(Runnable {

                // 2020.11.18 / 문성찬 / 게시판 표시 시 검색어와 스피너 값 초기화 작성
                et_search.setText("")
                spinner.setSelection(0)

                // 2020.11.02 / 문성찬 / spinner를 이용한 tag별 게시판 글 표시 구현
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        var adapter = RecyclerAdapter(postList)
                        rv_post.adapter = adapter
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        var selectedPostList: ArrayList<getPost> = arrayListOf()

                        if(items[position] != "전체"){
                            for(i in postList){
                                if(i.tag == items[position]){
                                    selectedPostList.add(i)
                                }
                            }
                        }else{
                            selectedPostList = postList
                        }

                        var adapter = RecyclerAdapter(selectedPostList)
                        // 2020.11.18 / 문성찬 / 분류 후 리사이클뷰 클릭 시 인텐트로 액티비티 안 넘어가는 오류 해결
                        adapter.setItemClickListener(object : RecyclerAdapter.itemClickListener{
                            override fun onClick(view: View, position: Int) {
                                // 넘어가는 인텐트 로그캣
                                Log.d("check","인덱스 : ${position}")
                                Log.d("check","title : ${selectedPostList[position].title}")
                                Log.d("check","content : ${selectedPostList[position].content}")

                                val intent = Intent(context, PostViewActivity::class.java)
                                intent.putExtra("title", selectedPostList[position].title)
                                intent.putExtra("content", selectedPostList[position].content)
                                intent.putExtra("time", selectedPostList[position].time)
                                intent.putExtra("image", selectedPostList[position].image)
                                intent.putExtra("author", selectedPostList[position].author)
                                intent.putExtra("nickname", selectedPostList[position].nickname)
                                intent.putExtra("goodCount", selectedPostList[position].goodCount)
                                intent.putExtra("scrapCount", selectedPostList[position].scrapCount)

                                startActivityForResult(intent, 0)
                            }
                        })
                        rv_post.adapter = adapter
                    }
                }

                // 2020.11.02 / 문성찬 / 검색 기능 구현
                btn_search.setOnClickListener {
                    val searchedString : String = et_search.text.toString()
                    var selectedPostList: ArrayList<getPost> = arrayListOf()
                    val selectedTag = spinner.selectedItem
                    Log.d("check","현재 spinner 값 : ${selectedTag}")

                    if(searchedString == ""){
                        if(selectedTag != "전체"){
                            for(i in postList){
                                if(i.tag == selectedTag){
                                    selectedPostList.add(i)
                                }
                            }
                        }else{
                            selectedPostList = postList
                        }
                    }else{
                        if(selectedTag != "전체"){
                            for(i in postList){
                                if(i.tag == selectedTag && i.title.contains(searchedString)){
                                    selectedPostList.add(i)
                                }
                            }
                        }else{
                            for(i in postList){
                                if(i.title.contains(searchedString)){
                                    selectedPostList.add(i)
                                }
                            }
                        }
                    }
                    if(selectedPostList.size == 0){
                        Toast.makeText(activity,"찾으시는 ${searchedString}이(가) 없습니다.", Toast.LENGTH_LONG).show()
                    }

                    var adapter = RecyclerAdapter(selectedPostList)
                    // 2020.11.18 / 문성찬 / 검색 후 리사이클뷰 클릭 시 인텐트로 액티비티 안 넘어가는 오류 해결
                    adapter.setItemClickListener(object : RecyclerAdapter.itemClickListener{
                        override fun onClick(view: View, position: Int) {
                            // 넘어가는 인텐트 로그캣
                            Log.d("check","인덱스 : ${position}")
                            Log.d("check","title : ${selectedPostList[position].title}")
                            Log.d("check","content : ${selectedPostList[position].content}")

                            val intent = Intent(context, PostViewActivity::class.java)
                            intent.putExtra("title", selectedPostList[position].title)
                            intent.putExtra("content", selectedPostList[position].content)
                            intent.putExtra("time", selectedPostList[position].time)
                            intent.putExtra("image", selectedPostList[position].image)
                            intent.putExtra("author", selectedPostList[position].author)
                            intent.putExtra("nickname", selectedPostList[position].nickname)
                            intent.putExtra("goodCount", selectedPostList[position].goodCount)
                            intent.putExtra("scrapCount", selectedPostList[position].scrapCount)

                            startActivityForResult(intent, 0)
                        }
                    })
                    rv_post.adapter = adapter
                }

                var adapter = RecyclerAdapter(postList)

                // 2020.10.28 / 문성찬 / 리사이클뷰 클릭 시 인텐트로 액티비티 넘김
                adapter.setItemClickListener(object : RecyclerAdapter.itemClickListener{
                    override fun onClick(view: View, position: Int) {
                        // 넘어가는 인텐트 로그캣
                        Log.d("check","인덱스 : ${position}")
                        Log.d("check","title : ${postList[position].title}")
                        Log.d("check","content : ${postList[position].content}")

                        val intent = Intent(context, PostViewActivity::class.java)
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
                })

                rv_post.adapter = adapter
            })
        }
        postList = arrayListOf()
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
    }


}