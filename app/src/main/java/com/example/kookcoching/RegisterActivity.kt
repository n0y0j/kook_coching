package com.example.kookcoching

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


// 2020.11.04 / 노용준 / 회원가입 화면
class RegisterActivity : AppCompatActivity() {

    lateinit var NameText: EditText
    lateinit var IdText: EditText
    lateinit var PasswordText: EditText
    lateinit var CheckPasswordText: EditText
    lateinit var registerBtn : Button
    lateinit var backBtn : Button
    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        backBtn = findViewById(R.id.back)
        registerBtn = findViewById(R.id.register_btn)

        backBtn.setOnClickListener {
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        NameText = findViewById(R.id.register_name)
        IdText = findViewById(R.id.register_id)
        PasswordText = findViewById(R.id.register_password)
        CheckPasswordText = findViewById(R.id.register_pasword_check)

        registerBtn.setOnClickListener {
            val name = NameText.text.toString()
            val id = IdText.text.toString()
            val password = PasswordText.text.toString()
            val check_password = CheckPasswordText.text.toString()

            // 비밀번호와 비밀번호 체크가 같은지 확인
            if (password.equals(check_password)) {
                Log.d("CHECK", "등록: " + id + " " + password)

                // 새로운 user 생성
                firebaseAuth.createUserWithEmailAndPassword(id, password).addOnCompleteListener(
                    object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            // 성공했다면 Database에 User의 정보를 저장
                            if (p0.isSuccessful()) {
                                val user = firebaseAuth.currentUser as FirebaseUser
                                val user_id = user.email
                                val user_uid = user.uid
                                val user_name = NameText.text.toString()

                                val hashMap: HashMap<Any, String> = HashMap()

                                hashMap.put("uid", user_uid)
                                hashMap.put("id", user_id.toString())
                                hashMap.put("name", user_name)

                                val database = FirebaseDatabase.getInstance()
                                val reference = database.getReference("User")
                                reference.child(user_uid).setValue(hashMap)

                                Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                finish()
                            }
                            // 이미 존재하는 아이디가 있는 경우
                            else {
                                Toast.makeText(this@RegisterActivity, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                )
            }
            else {
                Toast.makeText(this@RegisterActivity, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}