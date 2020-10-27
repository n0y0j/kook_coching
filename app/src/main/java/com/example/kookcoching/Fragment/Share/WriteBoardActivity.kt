package com.example.kookcoching.Fragment.Share

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.kookcoching.Fragment.ShareBoardFragment
import com.example.kookcoching.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class WriteBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board)

        val btn_cancel = findViewById(R.id.btn_cancel) as Button
        val btn_store = findViewById(R.id.btn_store) as Button
        val chipGroup = findViewById(R.id.chip_group) as ChipGroup
        var tag : String = ""

        val share_chip_string: ArrayList<String> = arrayListOf("알고리즘", "앱", "웹")

        var intent: Intent = getIntent()
        val chip_count: String? = intent.getStringExtra("chip_type")

        // 2020.10.26 / 노용준 / 게시판 별로 tag 생성
        when {
            chip_count == "share" ->
                for ( name in share_chip_string ) {
                    var chip  = Chip(this)
                    chip.setText(name)
                    chip.isCheckable = true
                    chipGroup.addView(chip)

                }
        }

        chipGroup.setOnCheckedChangeListener{ group, chech_pos:Int ->

            val chip:Chip? = findViewById(chech_pos)
            tag = chip?.text.toString()
            Log.d("asd", tag)

        }


        btn_cancel.setOnClickListener {
            finish()
        }

        btn_store.setOnClickListener {
            val title = findViewById(R.id.et_title) as EditText
            val content = findViewById<EditText>(R.id.et_content)
            var fbFirestore: FirebaseFirestore? = null

            fbFirestore = FirebaseFirestore.getInstance() // firestore 인스턴스 초기화
            fbFirestore?.collection("share_post")?.document(title.text.toString())
                ?.set(Post(title.text.toString(),content.text.toString(), System.currentTimeMillis(), tag))
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

