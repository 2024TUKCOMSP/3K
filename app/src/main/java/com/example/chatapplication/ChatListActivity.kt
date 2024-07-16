package com.example.chatapplication

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
}