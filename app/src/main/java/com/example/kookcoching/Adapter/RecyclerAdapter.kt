package com.example.kookcoching.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Fragment.Share.Post
import com.example.kookcoching.R
import kotlinx.coroutines.CoroutineScope

// 2020.10.26 / 문성찬 / 리사이클뷰 어댑터 기능
class RecyclerAdapter(val itemList: ArrayList<Post>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    // 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item,parent,false)
        return RecyclerAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // 위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(itemList[position])
    }

    // 카드뷰 xml을 이용해 리사이클뷰 아이템 표시
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val title = itemView?.findViewById<TextView>(R.id.cardView_title)
        val content = itemView?.findViewById<TextView>(R.id.cardView_content)

        fun bind (post: Post){
            title?.text = post.title
            content?.text = post.content
        }
    }

}
