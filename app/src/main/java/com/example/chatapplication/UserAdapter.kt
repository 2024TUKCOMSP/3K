package com.example.chatapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.databinding.ItemFriendBinding



class UserAdapter(val userList: ArrayList<User>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class UserViewHolder(val binding: ItemFriendBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
    RecyclerView.ViewHolder = UserViewHolder(
        ItemFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as UserViewHolder).binding
        binding.frienditemTextview.text = userList[position].name
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}