package com.example.kookcoching

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

// 2020.11.04 / 노용준 / 로그인 화면
// 입력받은 Email과 Password를 통해 FireBase Authentication에 저장된 유저인지 아닌지 확인한다.
class LoginActivity : AppCompatActivity() {

    lateinit var EmailText: EditText
    lateinit var PasswordText: EditText
    lateinit var loginBtn : Button
    lateinit var regiBtn : TextView
    lateinit var passBtn : TextView
    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn = findViewById(R.id.login_btn)
        regiBtn = findViewById(R.id.regi_btn)
        passBtn = findViewById(R.id.search_btn)

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
                }else {
                    Toast.makeText(this@LoginActivity, "등록된 사용자가 아닙니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 2020.11.11 / 노용준 / 비밀번호 찾기
        // 등록되어 있는 Email 입력 시, 비밀번호 재설정 메일을 전송한다.
        passBtn.setOnClickListener {
            val email : EditText = EditText(this);

            val builder : AlertDialog.Builder = AlertDialog.Builder(this);
            builder.setTitle("비밀번호 찾기");
            builder.setMessage("이메일을 입력해주세요");
            builder.setView(email);
            builder.setPositiveButton("보내기",
                object: DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString())
                            .addOnCompleteListener {task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(getApplicationContext(), "재설정 메일을 보냈습니다." ,Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "이메일을 확인해주세요" ,Toast.LENGTH_LONG).show();
                                }
                            }

                    }

                })
            builder.setNegativeButton("취소",
                object: DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                });
            builder.show();
        }


    }
}