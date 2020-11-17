package com.example.kookcoching.Fragment.Board

import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.kookcoching.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WriteBoardActivity : AppCompatActivity() {

    private var mStorageRef: StorageReference? = null
    private var fbFirestore: FirebaseFirestore? = null
    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_board)

        firebaseAuth = FirebaseAuth.getInstance()
        val btn_cancel = findViewById(R.id.btn_cancel) as ImageButton
        val btn_store = findViewById(R.id.btn_store) as Button
        val btn_camera = findViewById(R.id.cameta_btn) as ImageButton
        var et_title = findViewById<EditText>(R.id.et_title)
        var et_content = findViewById<EditText>(R.id.et_content)
        val image_linear = findViewById<LinearLayout>(R.id.image_linear)

        val chipGroup = findViewById(R.id.chip_group) as ChipGroup
        var tag: String = ""

        // 2020.11.14 / 문성찬 / 전공 게시판, 프로젝트 게시판에도 적용되게끔 추가
        val share_chip_string: ArrayList<String> = arrayListOf("알고리즘", "앱", "웹")
        val major_chip_string: ArrayList<String> = arrayListOf(
            "이산수학",
            "모바일 프로그래밍",
            "컴퓨터구조",
            "응용통계학",
            "화일처리"
        )
        val project_chip_string: ArrayList<String> = arrayListOf("알고리즘", "앱", "웹")


        // 수정할 때 기존 데이터를 이동하기 위한 intent
        var intent: Intent = getIntent()
        var before_title = intent.getStringExtra("before_title")
        var before_content = intent.getStringExtra("before_content")
        var before_time = intent.getLongExtra("before_time", 0)
        var check = intent.getStringExtra("check")

        val chip_count: String? = intent.getStringExtra("chip_type")
        var selectUrlList: List<Uri> = listOf()



        btn_camera.setOnClickListener {

            // 2020.10.30 / 노용준 / 앨범 권한에 대한 Permission과 Multi Image Picker
            // 권한 허용 시 onPermissionGranted() 함수 실행
            // 권한 거부 시 onPermissionDenied() 함수 실행
            var permissionlistener: PermissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
                    selectUrlList = listOf()
                    Toast.makeText(this@WriteBoardActivity, "권한 허가", Toast.LENGTH_SHORT).show()

                    // Multi Image Picker
                    TedBottomPicker.with(this@WriteBoardActivity)
                        .setPeekHeight(1600)
                        .showTitle(false)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Select")
                        .setSelectedUriList(selectUrlList)
                        .showMultiImage(object :
                            TedBottomSheetDialogFragment.OnMultiImageSelectedListener {
                            override fun onImagesSelected(uriList: List<Uri>) {
                                Log.d("image", uriList.toString())

                                for (item in uriList) {
                                    // 이미지의 미리보기를 위해 ImageView 동적 생성
                                    val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(130,
                                        130
                                    )
                                    lp.setMargins(20, 0, 0, 0)

                                    val myImage = ImageView(applicationContext)
                                    myImage.maxWidth = 100
                                    myImage.maxHeight = 100
                                    myImage.setImageURI(item)
                                    myImage.layoutParams = lp

                                    image_linear.addView(myImage)

                                }

                                selectUrlList = uriList
                            }
                        })

                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Toast.makeText(
                        this@WriteBoardActivity,
                        "권한 거부" + deniedPermissions.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // 권한 요청
            ActivityCompat.requestPermissions(
                this@WriteBoardActivity, arrayOf<String>(
                    READ_CONTACTS,
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE
                ),
                1
            )

            TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
                .check()

        }

        // 2020.10.26 / 노용준 / 게시판 별로 tag 생성
        // 2020.11.14 / 문성찬 / 전공 게시판, 프로젝트 게시판에도 적용되게끔 추가
        when {
            chip_count == "share" ->
                for (name in share_chip_string) {
                    var chip = Chip(this)
                    chip.setText(name)
                    chip.isClickable = true
                    chip.isCheckable = true
                    chipGroup.addView(chip)
                }
            chip_count == "major" ->
                for (name in major_chip_string) {
                    var chip = Chip(this)
                    chip.setText(name)
                    chip.isClickable = true
                    chip.isCheckable = true
                    chipGroup.addView(chip)
                }
            chip_count == "project" ->
                for (name in project_chip_string) {
                    var chip = Chip(this)
                    chip.setText(name)
                    chip.isClickable = true
                    chip.isCheckable = true
                    chipGroup.addView(chip)
                }
        }

        chipGroup.setOnCheckedChangeListener { group, check_pos: Int ->

            val chip: Chip? = findViewById(check_pos)

            tag = chip?.text.toString()
        }


        btn_cancel.setOnClickListener {
            finish()
        }
        // 2020.11.2 / 노성환 / 게시글 수정하면 firestore 게시글의 필드값 수정
        // 2020.11.14 / 문성찬 / 전공 게시판, 프로젝트 게시판에도 적용되게끔 추가
        // 2020.11.17 / 문성찬 / 게시글 수정 시 아무것도 입력 안했을 때의 수행처리되는 에러 수정
        // 수정을 하면 게시판을 수정한 후 업데이트
        if (check == "update") {
            Log.d("CHECK, TIME", check.toString() + ", " + before_time.toString())
            et_title.setText(before_title).toString()
            et_content.setText(before_content).toString()
            btn_store.setOnClickListener {

                fbFirestore = FirebaseFirestore.getInstance()
                var update = fbFirestore!!.collection("share_post").document(before_time.toString())
                when {
                    chip_count == "share" ->
                        update = fbFirestore!!.collection("share_post").document(before_time.toString())
                    chip_count == "major" ->
                        update = fbFirestore!!.collection("major_post").document(before_time.toString())
                    chip_count == "project" ->
                        update = fbFirestore!!.collection("project_post").document(before_time.toString())
                }
                if((et_title.text.toString().length == 0) || (et_content.text.toString().length == 0) || (tag.length == 0)){
                    Toast.makeText(this@WriteBoardActivity, "게시글을 다시 한번 확인해주세요.",Toast.LENGTH_LONG).show()
                }else {
                    update
                        .update("title", et_title.text.toString())
                    update
                        .update("content", et_content.text.toString())
                    update.update("tag", tag)
                        .addOnCompleteListener {
                            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                            Thread.sleep(1000L)
                            finish()
                        }
                }
            }


        }
        // 게시판을  새로 만들면 추가
        else if (check == "new") {
            // 노성환 / firestore 게시판 컬렉션에 저장
            btn_store.setOnClickListener {
                val title = findViewById(R.id.et_title) as EditText
                val content = findViewById<EditText>(R.id.et_content)
                val id = System.currentTimeMillis().toString()

                var downloadUri: ArrayList<String> = arrayListOf()

                fbFirestore = FirebaseFirestore.getInstance() // firestore 인스턴스 초기화

                // 2020.11.11 / 노성환 / firestore user컬렉션에서 닉네임 활용(for post)
                var name: String ?= null
                var docRef = fbFirestore!!.collection("user").document(firebaseAuth.currentUser?.uid.toString())
                docRef.get()
                    .addOnSuccessListener { document ->
                        name = document.get("name").toString()
                    }

                // 2020.10.30 / 노용준 / 선택된 Image를 Firestore에 저장
                // FireStorage에 저장 후 Image Url을 파싱해서 Firestore에 저장
                mStorageRef = FirebaseStorage.getInstance().getReference()

                val scope = CoroutineScope(Dispatchers.Default)

                scope.launch {
                    val job = launch {
                        for (item in selectUrlList) {
                            val temp = item.toString().split("/")
                            val name = temp[temp.size - 1]

                            var imageRef = mStorageRef!!.child(id + "/" + name)
                            val uploadTask = imageRef.putFile(item)

                            uploadTask.continueWith {
                                imageRef.downloadUrl
                            }.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    it.result!!.addOnSuccessListener { task ->
                                        downloadUri.add(task.toString())
                                    }
                                }
                            }.await()
                        }
                    }

                    job.join()

                    val handler = Handler(Looper.getMainLooper())

                    handler.postDelayed(Runnable {
                        // 2020.10.29 / 노용준 / document name을 epoch time으로 설정 (시간 순으로 자동정렬)
                        // 2020.11.14 / 문성찬 / 전공 게시판, 프로젝트 게시판에도 적용되게끔 추가
                        // 2020.11.17 / 문성찬 / 게시글 작성 시 아무것도 입력 안했을 때의 수행처리되는 에러 수정
                        if((title.text.toString().length == 0) || (content.text.toString().length == 0) || (tag.length == 0)){
                            Toast.makeText(this@WriteBoardActivity, "게시글을 다시 한번 확인해주세요.",Toast.LENGTH_LONG).show()
                        }else {
                            when {
                                chip_count == "share" ->
                                    fbFirestore?.collection("share_post")?.document(id)
                                        ?.set(
                                            Post(
                                                title.text.toString(),
                                                content.text.toString(),
                                                downloadUri,
                                                tag,
                                                firebaseAuth.currentUser?.uid.toString(),
                                                name.toString(),
                                                arrayListOf(),
                                                arrayListOf()
                                            )
                                        )
                                chip_count == "major" ->
                                    fbFirestore?.collection("major_post")?.document(id)
                                        ?.set(
                                            Post(
                                                title.text.toString(),
                                                content.text.toString(),
                                                downloadUri,
                                                tag,
                                                firebaseAuth.currentUser?.uid.toString(),
                                                name.toString(),
                                                arrayListOf(),
                                                arrayListOf()
                                            )
                                        )
                                chip_count == "project" ->
                                    fbFirestore?.collection("project_post")?.document(id)
                                        ?.set(
                                            Post(
                                                title.text.toString(),
                                                content.text.toString(),
                                                downloadUri,
                                                tag,
                                                firebaseAuth.currentUser?.uid.toString(),
                                                name.toString(),
                                                arrayListOf(),
                                                arrayListOf()
                                            )
                                        )
                            }
                            Toast.makeText(this@WriteBoardActivity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }, 500)
                }
            }
        }


    }
}

