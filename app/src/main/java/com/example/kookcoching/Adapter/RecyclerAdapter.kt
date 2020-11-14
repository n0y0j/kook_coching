package com.example.kookcoching.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Fragment.Share.getPost
import com.example.kookcoching.R
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

// 2020.10.26 / 문성찬 / 리사이클뷰 어댑터 기능
class RecyclerAdapter(val itemList: ArrayList<getPost>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    // 2020.10.28 / 문성찬 / 리사이클뷰 클릭 이벤트 구현
    // 데어터를 저장할 아이템 리스트
    val items = ArrayList<getPost>()

    // 클릭 인터페이스 정의
    interface itemClickListener{
        fun onClick(view:View, position: Int)
    }

    // 클릭 리스너 선언
    private lateinit var itemClick: itemClickListener

    // 클릭 리스너 등록 메소드
    fun setItemClickListener(itemClickListener: itemClickListener){
        this.itemClick = itemClickListener
    }

    // 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // 위의 onCreateViewHolder에서 만든 view와 실제 입력되는 각각의 데이터를 연결
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bind(itemList[position], position)

        // view에 onClickListener를 달고, 그 안에서 직접 만든 itemClickListener를 연결
        holder.itemView.setOnClickListener {
            itemClick.onClick(it,position)
        }
    }

    // 카드뷰 xml을 이용해 리사이클뷰 아이템 표시
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tag = itemView?.findViewById<TextView>(R.id.cardView_tag)
        val title = itemView?.findViewById<TextView>(R.id.cardView_title)
        val content = itemView?.findViewById<TextView>(R.id.cardView_content)
        val writer = itemView?.findViewById<TextView>(R.id.cardView_writer)
        val goodCount = itemView?.findViewById<TextView>(R.id.good_count)
        val scrapCount = itemView?.findViewById<TextView>(R.id.scrap_count)

        fun bind (post: getPost, num: Int){

            tag?.text = "[" + post.tag + "]"
            title?.text = post.title
            content?.text = post.content
            writer?.text = post.nickname
            goodCount?.text = post.goodCount.size.toString()
            scrapCount?.text = post.scrapCount.size.toString()
        }
    }
}
