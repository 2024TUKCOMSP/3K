package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.databinding.ActivityChatListBinding
import com.example.chatapplication.fragment.PeopleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatListActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bnv_main = findViewById(R.id.chatlist_bottomnavigationview) as BottomNavigationView
        bnv_main.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.action_people -> {
                        val peopleFragment = PeopleFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.chatlist_framelayout,
                            peopleFragment).commit()
                    }
                }
                true
            }
            selectedItemId = R.id.action_people
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0,0,0,"로그아웃")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            0 -> {
                AlertDialog.Builder(this).run{
                    setTitle("로그아웃")
                    setIcon(android.R.drawable.ic_dialog_info)
                    setMessage("로그아웃하시겠습니까?")
                    setPositiveButton("네"){_,_ ->
                        startActivity(Intent(this@ChatListActivity, LoginActivity::class.java))
                        finish()
                    }
                    setNegativeButton("아니오", null)
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}