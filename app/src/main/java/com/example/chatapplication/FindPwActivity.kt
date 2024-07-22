package com.example.chatapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivityFindPwBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase

class FindPwActivity : AppCompatActivity() {
    lateinit var binding:ActivityFindPwBinding
    lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        binding.findPwBtn.setOnClickListener{
            val name = binding.nameEdit.text.toString().trim()
            val email = binding.emailEdit.text.toString().trim()

            if(name.isEmpty() || email.isEmpty()){
                Toast.makeText(this, "이름과 이메일을 모두 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "이메일을 전송했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this, "존재하지 않는 사용자이거나 잘못된 이메일입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}