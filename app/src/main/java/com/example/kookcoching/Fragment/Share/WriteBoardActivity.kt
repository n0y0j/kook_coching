package com.example.kookcoching.Fragment.Share

import android.Manifest
import android.Manifest.permission.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import com.example.kookcoching.Fragment.ShareBoardFragment
import com.example.kookcoching.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment

class WriteBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board)

        val btn_cancel = findViewById(R.id.btn_cancel) as Button
        val btn_store = findViewById(R.id.btn_store) as Button
        val btn_camera = findViewById(R.id.cameta_btn) as ImageButton

        val chipGroup = findViewById(R.id.chip_group) as ChipGroup
        var tag : String = ""

        val share_chip_string: ArrayList<String> = arrayListOf("알고리즘", "앱", "웹")

        var intent: Intent = getIntent()
        val chip_count: String? = intent.getStringExtra("chip_type")

        btn_camera.setOnClickListener{

            // 2020.10.30 / 노용준 / 앨범 권한에 대한 Permission
            var permissionlistener:PermissionListener = object: PermissionListener {
                override fun onPermissionGranted() {
                    var selectUrlList:List<Uri> = listOf()
                    Toast.makeText(this@WriteBoardActivity, "권한 허가", Toast.LENGTH_SHORT).show()

                }
                override fun onPermissionDenied(deniedPermissions:List<String>) {
                    Toast.makeText(this@WriteBoardActivity, "권한 거부" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            ActivityCompat.requestPermissions(this@WriteBoardActivity, arrayOf<String>(READ_CONTACTS, WRITE_EXTERNAL_STORAGE),
                1)

            TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(READ_CONTACTS, WRITE_EXTERNAL_STORAGE)
                .check()

        }



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

        // 노성환 / firestore 게시판 컬렉션에 저장
        btn_store.setOnClickListener {
            val title = findViewById(R.id.et_title) as EditText
            val content = findViewById<EditText>(R.id.et_content)
            var fbFirestore: FirebaseFirestore? = null

            fbFirestore = FirebaseFirestore.getInstance() // firestore 인스턴스 초기화

            // 2020.10.29 / 노용준 / document name을 epoch time으로 설정 (시간 순으로 자동정렬)
            fbFirestore?.collection("share_post")?.document(System.currentTimeMillis().toString())
                ?.set(Post(title.text.toString(),content.text.toString(), tag))
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}

