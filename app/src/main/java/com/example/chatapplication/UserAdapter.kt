package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.databinding.ItemFriendBinding


class UserAdapter(val context: Context, val userList: ArrayList<User>):
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
        val currentUser = userList[position]
        binding.frienditemTextview.text = currentUser.name

        holder.itemView.setOnClickListener{
            val intent = Intent(context, chatActivity::class.java)

            intent.putExtra("name", currentUser.name)
            intent.putExtra("uId", currentUser.uId)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}