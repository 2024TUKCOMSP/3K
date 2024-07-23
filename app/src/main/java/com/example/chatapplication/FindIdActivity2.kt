package com.example.chatapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivityFindId2Binding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FindIdActivity2 : AppCompatActivity() {
    lateinit var binding:ActivityFindId2Binding
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindId2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")

        if (name != null && phone != null) {
            findUserId(name, phone)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun findUserId(name: String, phone: String){
        mDbRef = FirebaseDatabase.getInstance().reference.child("user")
        mDbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var found = false
                for(data in snapshot.children){
                    val userName = data.child("name").getValue()
                    val userPhone = data.child("phone").getValue()

                    if(userName != null && userName == name && userPhone != null && userPhone == phone){
                        val userId = data.child("email").getValue()
                        binding.idText.text = "찾으시는 ID는 $userId 입니다."
                        found = true
                    }
                }

                if(!found){
                    binding.idText.text = "일치하는 정보가 없습니다."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.idText.text = "오류가 발생했습니다. 다시 시도해 주세요."
            }

        })
    }
}