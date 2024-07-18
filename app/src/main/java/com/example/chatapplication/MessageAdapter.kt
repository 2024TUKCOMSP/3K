package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>):
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
        } else {
            val viewHolder = holder as RecvViewHolder
            viewHolder.recvMessage.text = currentMessage.message
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
    }

    // 받은 쪽
    class RecvViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val recvMessage: TextView = itemView.findViewById(R.id.recv_text_message)
    }
}