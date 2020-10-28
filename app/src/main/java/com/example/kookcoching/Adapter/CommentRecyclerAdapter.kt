package com.example.kookcoching.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Fragment.Share.Comment
import com.example.kookcoching.R
import java.text.SimpleDateFormat
import java.util.*

// 2020.10.26 / 문성찬 / 댓글 리사이클뷰 어댑터 기능
class CommentRecyclerAdapter(val commentList: ArrayList<Comment>):
    RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {

    // 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_comment,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    // 위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(commentList[position], position)
    }

    // 카드뷰 xml을 이용해 리사이클뷰 아이템 표시
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val comment = itemView?.findViewById<TextView>(R.id.tv_comment)
        val commentTime = itemView?.findViewById<TextView>(R.id.tv_commentTime)

        fun bind (data: Comment, num: Int){
            comment?.text = data.comment

            // 2020.10.28 / 노용준 / epoch time to date
            val itemDate = Date(data.commentTime)
            val dateFormat = SimpleDateFormat("MM/dd HH:mm")
            dateFormat.timeZone = TimeZone.getTimeZone("GMT+09:00")
            val date = dateFormat.format(itemDate)
            commentTime?.text = date

        }
    }


}