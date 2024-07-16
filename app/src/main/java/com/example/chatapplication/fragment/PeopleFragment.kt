package com.example.chatapplication.fragment

import android.content.Context
import android.os.Bundle
import android.text.InputFilter.AllCaps
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.User
import com.example.chatapplication.UserAdapter
import com.example.chatapplication.databinding.FragmentPeopleBinding

class PeopleFragment : Fragment() {
    lateinit var binding: FragmentPeopleBinding
    lateinit var adapter: UserAdapter

    lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentPeopleBinding.inflate(layoutInflater)
        userList = ArrayList()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)

        /*
        adapter = UserAdapter(requireContext(), userList)

        binding.peoplefragmentRecyclerview.layoutManager = LinearLayoutManager(this.context)
        binding.peoplefragmentRecyclerview.adapter = adapter
        binding.peoplefragmentRecyclerview.addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        adapter.notifyDataSetChanged()*/
        return view
    }
}