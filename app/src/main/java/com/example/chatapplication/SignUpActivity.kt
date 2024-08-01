package com.example.chatapplication

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mAuth = Firebase.auth
        mDbRef = Firebase.database.reference


        binding.signUpBtn.setOnClickListener {
            val name = binding.nameEdit.text.toString().trim()
            val email = binding.emailEdit.text.toString().trim()
            val password = binding.passwordEdit.text.toString().trim()
            val phone = binding.phoneEdit.text.toString().trim()

            if(email == "" || phone == "" || password == "" || name == "") {
                Toast.makeText(this, "형식이 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                if(duplicateCheck(email, phone)){
                    signUp(name, phone, email, password)
                }
            }
        }
    }

    private fun duplicateCheck(email: String, phone: String): Boolean {
        var dpCheck = true
        mDbRef = FirebaseDatabase.getInstance().reference.child("user")
        mDbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    val userEmail = data.child("email").getValue()
                    val userPhone = data.child("phone").getValue()

                    if(userEmail == email){
                        Toast.makeText(this@SignUpActivity, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show()
                        dpCheck = false
                    }
                    if(userPhone == phone){
                        Toast.makeText(this@SignUpActivity, "이미 가입된 번호입니다.", Toast.LENGTH_SHORT).show()
                        dpCheck = false
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUpActivity, "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                dpCheck = false
            }
        })
        return dpCheck
    }

    private fun signUp(name: String, phone: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                    addUserToDatabase(name, phone, email, mAuth.currentUser?.uid!!)
                    finish()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, phone: String, email:String, uId: String){
        mDbRef = Firebase.database.reference
        val storage = Firebase.storage
        val storageRef = storage.getReference("image")
        val mountainsRef = storageRef.child("${uId}.png")
        mountainsRef.putFile(getResourceUri(this))
        mDbRef.child("user").child(uId).setValue(User(name, phone, email, uId, Font(14, "maruburibold")))
    }

    // drawable 파일 Uri
    fun getResourceUri(context: Context): Uri {
        val uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.resources.getResourcePackageName(R.drawable.default_image)
                + '/' + context.resources.getResourceTypeName(R.drawable.default_image) + '/' + context.resources.getResourceEntryName(R.drawable.default_image));
        return uri
    }
}