package com.example.chatapplication

import android.content.Intent
import android.content.SharedPreferences
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var sharedPref: SharedPreferences

    var RC_SIGN_IN = 9000

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        sharedPref = getSharedPreferences("com.example.chatapplication_preferences", MODE_PRIVATE)



        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            if (!email.isEmpty() || !password.isEmpty())
                login(email, password)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.my_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignUpBtn.setOnClickListener {
            signIn()
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 성공 시 실행
                    sharedPref.edit().run {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        val mDbRef = Firebase.database.reference
                        putString("uid", uid)
                        mDbRef.child("user").child(uid).child("font")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (shot in snapshot.children) {
                                        putString(shot.key, shot.value.toString())
                                    }
                                    commit()
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                    val intent: Intent = Intent(
                        this@LoginActivity,
                        ChatListActivity::class.java
                    )
                    startActivity(intent)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // 실패 시 실행
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent //구글로그인 페이지로 가는 인텐트 객체
        startActivityForResult(signInIntent, RC_SIGN_IN) //Google Sign In flow 시작
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(
                    ApiException::class.java
                )
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!, account)
            } catch (e: ApiException) {
                Log.d("TAG", "로그인 실패", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(
                        applicationContext,
                        ChatListActivity::class.java
                    )
                    intent.putExtra("UserName", account.displayName)
                    intent.putExtra("PhotoUrl", account.photoUrl)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

