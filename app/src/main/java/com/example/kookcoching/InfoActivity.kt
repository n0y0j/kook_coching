package com.example.kookcoching

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 2020.11.12 / 노용준 / 내 정보 스크린
// 자신의 닉네임과 이메일을 확인할 수 있으며, 내가 쓴 글, 내가 좋아한 글, 내가 찜한 글 3가지 항목의
// 선택으로 자신이 원하는 글 목록을 확인할 수 있다. (+ 회원탈퇴)
class InfoActivity : AppCompatActivity() {

    lateinit var backButton : ImageButton
    lateinit var myLike : LinearLayout
    lateinit var myScrap : LinearLayout
    lateinit var myWrite: LinearLayout
    lateinit var deleteBtn : Button
    lateinit var tv_info_name: TextView
    lateinit var tv_info_mail: TextView
    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)

        backButton = findViewById(R.id.back_btn)
        myLike = findViewById(R.id.my_like_layout)
        myScrap = findViewById(R.id.my_scrap_layout)
        myWrite = findViewById(R.id.my_write_layout)
        deleteBtn = findViewById(R.id.my_user_delete)
        tv_info_name = findViewById(R.id.tv_info_name)
        tv_info_mail = findViewById(R.id.tv_info_email)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        var getIntent = getIntent()
        // 내 정보에서 현재 로그인된 아이디와 닉네임 띄우기
        tv_info_mail.setText(getIntent.getStringExtra("info_email"))
        tv_info_name.setText(getIntent.getStringExtra("info_name"))

        backButton.setOnClickListener {
            finish()
        }

        // 내가 좋아한 글 버튼 클릭 시
        myLike.setOnClickListener {
            val intent = Intent(this, MyPostActivity::class.java)
            intent.putExtra("name", "내가 좋아한 글")
            intent.putExtra("kind", "goodCount")
            startActivity(intent)
        }

        // 내가 찜한 글 버튼 클릭 시
        myScrap.setOnClickListener {
            val intent = Intent(this, MyPostActivity::class.java)
            intent.putExtra("name", "내가 찜한 글")
            intent.putExtra("kind", "scrapCount")
            startActivity(intent)
        }

        // 내가 쓴 글 버튼 클릭 시
        myWrite.setOnClickListener{
            val intent = Intent(this, MyPostActivity::class.java)
            intent.putExtra("name", "내가 쓴 글")
            intent.putExtra("kind", "write")
            startActivity(intent)
        }

        // 2020.11.12 / 노용준 / 회원탈퇴
        // 현재 접속된 유저의 정보를 Firebase Authentication에서 삭제한다.
        deleteBtn.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(this);
            builder.setTitle("회원탈퇴");
            builder.setMessage("회원탈퇴를 진행하시겠습니까?");
            builder.setPositiveButton("예",
                object: DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        firestore!!.collection("user")
                            .document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
                            .addOnCompleteListener {

                                FirebaseAuth.getInstance().currentUser!!.delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(applicationContext, "아이디 삭제가 완료되었습니다", Toast.LENGTH_LONG)
                                                .show()


                                            //로그아웃처리
                                            FirebaseAuth.getInstance().signOut()

                                            val intent = Intent(applicationContext, LoginActivity::class.java)
                                            startActivity(intent)


                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                task.exception.toString(),
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }

                                    }

                            }
                    }
                }
            )
            builder.setNegativeButton("아니요",
                object: DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                });
            builder.show();
        }



    }
}