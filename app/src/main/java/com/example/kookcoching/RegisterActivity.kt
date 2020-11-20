package com.example.kookcoching

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


// 2020.11.04 / 노용준 / 회원가입 화면
// FireAuthentication에 새로운 User를 등록한다.
// Email, Password, Name을 설정할 수 있다.
class RegisterActivity : AppCompatActivity() {

    lateinit var NameText: EditText
    lateinit var EmailText: EditText
    lateinit var PasswordText: EditText
    lateinit var CheckPasswordText: EditText
    lateinit var registerBtn : Button
    lateinit var backBtn : ImageButton
    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        backBtn = findViewById(R.id.back)
        registerBtn = findViewById(R.id.register_btn)

        // 뒤로가기 버튼
        backBtn.setOnClickListener {
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        NameText = findViewById(R.id.register_name)
        EmailText = findViewById(R.id.register_email)
        PasswordText = findViewById(R.id.register_password)
        CheckPasswordText = findViewById(R.id.register_pasword_check)

        // 회원가입 버튼 클릭 시
        registerBtn.setOnClickListener {
            val name = NameText.text.toString()
            val email = EmailText.text.toString()
            val password = PasswordText.text.toString()
            val check_password = CheckPasswordText.text.toString()

            if ( name != "" || email != "" || password != "" || check_password != "" ) {
                // 비밀번호와 비밀번호 체크가 같은지 확인
                if (password.equals(check_password)) {
                    Log.d("CHECK", "등록: " + email + " " + password)

                    // 새로운 user 생성
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { p0 ->
                        // 성공했다면 Firestore에 User의 정보를 저장
                        if (p0.isSuccessful) {
                            val user = firebaseAuth.currentUser as FirebaseUser
                            val user_id = user.email
                            val user_uid = user.uid
                            val user_name = NameText.text.toString()

                            val hashMap: HashMap<Any, String> = HashMap()

                            hashMap.put("id", user_id.toString())
                            hashMap.put("name", user_name)

                            val firestore = FirebaseFirestore.getInstance()

                            // user의 id, email을 firestore에 저장
                            firestore.collection("user").document(user_uid).set(hashMap)

                            Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT)
                                .show();
                            finish()
                        }
                        // 이미 존재하는 아이디가 있는 경우
                        else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "이미 존재하는 아이디 입니다.",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
                else {
                    Toast.makeText(this@RegisterActivity, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            else Toast.makeText(this@RegisterActivity, "입력 항목을 확인해주세요", Toast.LENGTH_SHORT).show()

        }
    }
}