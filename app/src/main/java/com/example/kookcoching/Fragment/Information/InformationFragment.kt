package com.example.kookcoching.Fragment.Information

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Fragment.Home.HomePagerAdapter
import com.example.kookcoching.Fragment.Home.TextViewScrolling.HomeSlotAdapter
import com.example.kookcoching.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class InformationFragment : Fragment() {

    val recyclerView : RecyclerView?= null
    var firestore : FirebaseFirestore?= null
    var postList : ArrayList<Post> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_information, container, false)
        val btn_move = view!!.findViewById(R.id.btn_moveToBoard) as Button
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
            Log.d(TAG, obj.size.toString())
            recyclerView?.adapter = RecyclerViewAdapter(obj)
            recyclerView?.layoutManager = LinearLayoutManager(context)

            for ( i in obj) {
                Log.d(TAG, " ${i.title} => ${i.content}")
            }

        }

        return view
    }

    inner class RecyclerViewAdapter(postList : ArrayList<Post>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){


        var postList : ArrayList<Post> = arrayListOf()

        init {
            this.postList = postList
            Log.d(TAG, "asdasweagasegsdagfdsagsdgsad")
        }


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title = itemView.findViewById<TextView>(R.id.cardView_title)
            val content = itemView.findViewById<TextView>(R.id.cardView_content)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = postList[position].title
            holder.content.text = postList[position].content

            Log.d(TAG, postList[position].title.toString())
            Log.d(TAG, "sadewhgfjewhfjkasdhgkdashgjkaweegegsadgjkaejflewj")


        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }

}