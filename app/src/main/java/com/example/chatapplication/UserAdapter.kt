package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.databinding.ItemFriendBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


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
        binding.frienditemId.text = currentUser.name

        val storage = Firebase.storage
        val storageRef = storage.getReference("image")
        val mountainsRef = storageRef.child("${currentUser.uId}.png")
        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            Glide.with(context).load(uri).into(binding.frienditemImage)
            binding.frienditemImage.clipToOutline = true
        }.addOnFailureListener {
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)

            intent.putExtra("name", currentUser.name)
            intent.putExtra("uId", currentUser.uId)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}