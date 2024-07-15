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

class ChatListActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener{
    lateinit var binding: ActivityChatListBinding
    lateinit var peopleFragment: PeopleFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        peopleFragment = PeopleFragment.newInstance()
        binding.chatlistBottomnavigationview.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_people -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.chatlist_framelayout, peopleFragment).commit()
            }
        }
        return true
    }
}