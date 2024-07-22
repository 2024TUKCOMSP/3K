package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>,
                     private val name: String, val size: Long, val style: Int):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val receive = 1 // 받는 타입
    private val send = 2 // 보내는 타입

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == receive) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_message_recv, parent, false)
            RecvViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_message_send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if(holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.message
            viewHolder.sendMessage.setTextSize(Dimension.SP, size.toFloat())
            viewHolder.sendMessage.typeface = ResourcesCompat.getFont(context, style)
        } else {
            val viewHolder = holder as RecvViewHolder
            viewHolder.recvName.text = name
            viewHolder.recvMessage.text = currentMessage.message
            viewHolder.recvMessage.setTextSize(Dimension.SP, size.toFloat())
            viewHolder.recvMessage.typeface = ResourcesCompat.getFont(context, style)

            val storage = Firebase.storage
            val storageRef = storage.getReference("image")
            val mountainsRef = storageRef.child("${currentMessage.sendId}.png")
            val downloadTask = mountainsRef.downloadUrl
            downloadTask.addOnSuccessListener { uri ->
                Glide.with(context).load(uri).into(viewHolder.recvImage)
                viewHolder.recvImage.clipToOutline = true
            }.addOnFailureListener {
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        //메시지 값
        val currentMessage = messageList[position]
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)) {
            send
        } else {
            receive
        }
    }

    // 보낸 쪽
    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.send_text_message)
        val sendTimestamp: TextView = itemView.findViewById(R.id.send_text_time)
    }

    // 받은 쪽
    class RecvViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val recvMessage: TextView = itemView.findViewById(R.id.recv_text_message)
        val recvName: TextView = itemView.findViewById(R.id.recv_text_name)
        val recvImage: ImageView = itemView.findViewById(R.id.recv_image_profile)
        val recvTimestamp: TextView = itemView.findViewById(R.id.recv_text_time)
    }
}