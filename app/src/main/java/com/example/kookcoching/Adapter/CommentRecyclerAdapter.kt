package com.example.kookcoching.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.Fragment.Share.PostViewActivity
import com.example.kookcoching.Fragment.Share.getComment
import com.example.kookcoching.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// 2020.10.26 / 문성찬 / 댓글 리사이클뷰 어댑터 기능
class CommentRecyclerAdapter(
    val commentList: ArrayList<getComment>,
    val postTime: String,
    val intent: Intent
) :
    RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {

    lateinit var firebaseAuth: FirebaseAuth
    var idx = -1

    // 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_comment, parent, false)
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
    // 2020.11.07 / 노성환 / 댓글의 삭제 버튼
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        val comment = itemView?.findViewById<TextView>(R.id.tv_comment)
        val commentTime = itemView?.findViewById<TextView>(R.id.tv_commentTime)
        val commentNickname = itemView?.findViewById<TextView>(R.id.tv_comment_nickname)
        val imageButton = itemView?.findViewById<ImageButton>(R.id.btn_menu)

        fun bind(data: getComment, num: Int) {
            firebaseAuth = FirebaseAuth.getInstance()
            comment?.text = data.comment
            commentNickname?.text = data.nickname // 댓글 닉네임 가져옴
            idx = num
            // 자신 계정이 맞는 경우에만 삭제 버튼이 생기도록 설정
            if (!(commentList[idx].author).equals(firebaseAuth.currentUser?.uid.toString()))
                imageButton.visibility = View.GONE
            imageButton.setOnClickListener(this)

            // 2020.10.28 / 노용준 / epoch time to date
            val itemDate = Date(data.time)
            val dateFormat = SimpleDateFormat("MM/dd HH:mm")
            dateFormat.timeZone = TimeZone.getTimeZone("GMT+09:00")
            val date = dateFormat.format(itemDate)
            commentTime?.text = date
        }

        // 2020.11.08 / 노성환 / 댓글 메뉴 구현
        override fun onClick(p0: View?) {
            showMenu(p0!!)
        }

        // 삭제 메뉴 보여줌
        fun showMenu(view: View) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.comment_menu)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    var db = FirebaseFirestore.getInstance()
                    when (item?.itemId) {
                        R.id.delete_comment -> {
                            Log.d("DELETE COMMENT", postTime)
                            db.collection("share_post").document(postTime)
                                .collection("share_post_comment")
                                .document(commentList[idx].time.toString()).delete()
                            val activity: PostViewActivity = view.context as PostViewActivity
                            activity.finish()
                            view.context.startActivity(intent)
                        }
                    }
                    idx = -1
                    return true
                }

            })
            popupMenu.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return true
        }


    }


}