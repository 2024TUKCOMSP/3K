package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivityFindIdBinding

class FindIdActivity : AppCompatActivity() {
    lateinit var binding:ActivityFindIdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.findIdBtn.setOnClickListener {
            val name = binding.nameEdit.text.toString().trim()
            val phone = binding.phoneEdit.text.toString().trim()

            if(name == "" || phone == ""){
                Toast.makeText(this, "이름과 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, FindIdActivity2::class.java)
            intent.putExtra("name", name)
            intent.putExtra("phone", phone)
            startActivity(intent)
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}