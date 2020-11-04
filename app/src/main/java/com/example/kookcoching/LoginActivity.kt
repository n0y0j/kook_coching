package com.example.kookcoching

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

// 2020.11.04 / 노용준 / 로그인 화면
class LoginActivity : AppCompatActivity() {

    lateinit var EmailText: EditText
    lateinit var PasswordText: EditText
    lateinit var loginBtn : Button
    lateinit var regiBtn : Button
    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn = findViewById(R.id.login_btn)
        regiBtn = findViewById(R.id.regi_btn)

        firebaseAuth = FirebaseAuth.getInstance()

        EmailText = findViewById(R.id.login_email)
        PasswordText = findViewById(R.id.login_password)

        // 회원가입 버튼 눌렀을 시
        regiBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 눌렀을 시
        loginBtn.setOnClickListener {
            val id = EmailText.text.toString()
            val password = PasswordText.text.toString()

            firebaseAuth.signInWithEmailAndPassword(id, password).addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    }
}