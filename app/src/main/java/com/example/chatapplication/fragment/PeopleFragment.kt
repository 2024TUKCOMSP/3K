package com.example.chatapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.R
import com.example.chatapplication.User
import com.example.chatapplication.UserAdapter
import com.example.chatapplication.databinding.FragmentPeopleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PeopleFragment : Fragment() {
    lateinit var binding: FragmentPeopleBinding
    lateinit var adapter: UserAdapter

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef:DatabaseReference

    lateinit var userList: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPeopleBinding.inflate(layoutInflater)

        // 인증 초기화
        mAuth = Firebase.auth
        // db 초기화
        mDbRef = Firebase.database.reference
        // 리스트 초기화
        userList = ArrayList()
        // 어댑터 초기화
        adapter = UserAdapter(requireContext(), userList)

        binding.peoplefragmentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.peoplefragmentRecyclerview.adapter = adapter
        binding.peoplefragmentRecyclerview.addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))

        mDbRef.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!userList.isEmpty())
                    userList = ArrayList()
                for(postSnapshot in snapshot.children){
                    // 유저 정보
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid != currentUser?.uId){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        return binding.root
    }
}
