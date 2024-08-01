package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.databinding.ItemFriendBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date


class UserAdapter(val context: Context, val userList: ArrayList<User>, val uid: String):
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
        binding.frienditemMessage.text = ""
        binding.frienditemTimestamp.text = ""

        val mDbRef = Firebase.database.reference
        val chat_room = uid + currentUser.uId
        mDbRef.child("chats").child(chat_room).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    for(postSnapshot in snapshot.children){
                        val readIndicatorString = postSnapshot.child("readIndicator").value?.toString()
                        val readIndicator = try {
                            readIndicatorString?.toInt() ?: 0
                        } catch (e: NumberFormatException) {
                            0
                        }
                        if(postSnapshot.child("sendId").value.toString() != uid)
                            count += readIndicator
                        val formatter = SimpleDateFormat("yyyy.MM.dd\nHH:mm")
                        val date = Date(postSnapshot.child("timestamp").value.toString().toLong())
                        binding.frienditemMessage.text = postSnapshot.child("message").value.toString()
                        binding.frienditemTimestamp.text = formatter.format(date)
                    }
                    if(count == 0) binding.frienditemUnRead.visibility = View.INVISIBLE
                    else {
                        binding.frienditemUnRead.visibility = View.VISIBLE
                        binding.frienditemUnRead.text = count.toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

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