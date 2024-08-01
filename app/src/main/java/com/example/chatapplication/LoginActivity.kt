package com.example.chatapplication

import android.content.Intent
import android.content.SharedPreferences
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.chatapplication.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var sharedPref: SharedPreferences

    var RC_SIGN_IN = 9000

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)

        mAuth = Firebase.auth
        sharedPref = getSharedPreferences("com.example.chatapplication_preferences", MODE_PRIVATE)

        binding.signUpBtn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()

            if(!email.isEmpty() && !password.isEmpty())
                login(email, password)
            else if(!email.isEmpty() || !password.isEmpty())
                Toast.makeText(this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.default_web_client_id))
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        KakaoSdk.init(this, getString(R.string.kakao_my_web))

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleSignUpBtn.setOnClickListener {
            signIn()
        }

        binding.kakaoSignUpBtn.setOnClickListener {
            kakaoLogin()
        }

        binding.findIdBtn.setOnClickListener {
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }

        binding.findPwBtn.setOnClickListener{
            val intent = Intent(this, FindPwActivity::class.java)
            startActivity(intent)
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
                    val intent = Intent(
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
                    val uId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    val mDbRef = Firebase.database.reference
                    val name = account.displayName.toString()
                    val email = account.email.toString()

                    mDbRef.child("user").child(uId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                // 데이터베이스에 해당 유저 정보가 없으면 저장
                                mDbRef.child("user").child(uId).setValue(User(name,"null", email, uId, Font(14, "maruburibold")))
                            }

                            // ChatListActivity로 이동
                            val intent = Intent(applicationContext, ChatListActivity::class.java)
                            startActivity(intent)
                            finish() // LoginActivity 종료
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // 에러 처리
                            Log.e("TAG", "데이터베이스 읽기 실패", error.toException())
                            Toast.makeText(this@LoginActivity, "데이터베이스 오류", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@LoginActivity, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun kakaoLogin() {
        // 카카오계정으로 로그인 공통 callback 구성
// 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("TAG", "카카오계정으로 로그인 실패1", error)
            } else if (token != null) {
                firebaseAuthWithCustomToken(token.idToken!!)

            }
        }

// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("TAG", "카카오톡으로 로그인 실패2", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i("TAG", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun firebaseAuthWithCustomToken(idToken: String) {
        val providerId = "oidc.kakao" // As registered in Firebase console.
        val credential = oAuthCredential(providerId) {
            setIdToken(idToken) // ID token from OpenID Connect flow.
        }
        Firebase.auth
            .signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val uId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val mDbRef = Firebase.database.reference
                lateinit var name: String
                lateinit var email: String
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("TAG", "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        Log.i("TAG", "사용자 정보 요청 성공")
                        name = user.kakaoAccount?.profile?.nickname.toString()
                        email = user.kakaoAccount?.email.toString()
                    }
                }

                mDbRef.child("user").child(uId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            // 데이터베이스에 해당 유저 정보가 없으면 저장
                            mDbRef.child("user").child(uId)
                                .setValue(User(name,"null",email, uId, Font(14, "maruburibold")))
                        }

                        // ChatListActivity로 이동
                        val intent = Intent(applicationContext, ChatListActivity::class.java)
                        startActivity(intent)
                        finish() // LoginActivity 종료
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // 에러 처리
                        Log.e("TAG", "데이터베이스 읽기 실패", error.toException())
                        Toast.makeText(this@LoginActivity, "데이터베이스 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "failure", e)
            }
    }
}

