package com.example.kookcoching.Fragment.Share

import android.Manifest.permission.*
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.kookcoching.Adapter.RecyclerAdapter
import com.example.kookcoching.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import gun0912.tedbottompicker.TedBottomSheetDialogFragment
import kotlinx.coroutines.*

class WriteBoardActivity : AppCompatActivity() {

    private var mStorageRef: StorageReference? = null
    private var fbFirestore: FirebaseFirestore? = null

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
        var selectUrlList:List<Uri> = listOf()

        btn_camera.setOnClickListener{

            // 2020.10.30 / 노용준 / 앨범 권한에 대한 Permission과 Multi Image Picker
            // 권한 허용 시 onPermissionGranted() 함수 실행
            // 권한 거부 시 onPermissionDenied() 함수 실행
            var permissionlistener:PermissionListener = object: PermissionListener {
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
        when {
            chip_count == "share" ->
                for ( name in share_chip_string ) {
                    var chip  = Chip(this)
                    chip.setText(name)
                    chip.isCheckable = true
                    chipGroup.addView(chip)

                }
        }

        chipGroup.setOnCheckedChangeListener{ group, chech_pos: Int ->

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
            val id = System.currentTimeMillis().toString()

            var downloadUri : ArrayList<String> = arrayListOf()

            fbFirestore = FirebaseFirestore.getInstance() // firestore 인스턴스 초기화

            // 2020.10.30 / 노용준 / 선택된 Image를 Firestore에 저장
            // FireStorage에 저장 후 Image Uri를 파싱해서 Firestore에 저장
            mStorageRef = FirebaseStorage.getInstance().getReference()

            val scope = CoroutineScope(Dispatchers.Default)

            scope.launch {
                val job = launch {
                    selectUrlList.forEach { item ->
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
                        }
                    }
                    Thread.sleep(2000L)
                }

                job.join()
                // 2020.10.29 / 노용준 / document name을 epoch time으로 설정 (시간 순으로 자동정렬)
                fbFirestore?.collection("share_post")?.document(id)
                    ?.set(Post(title.text.toString(), content.text.toString(), downloadUri, tag))
                finish()
            }
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

    }
}

