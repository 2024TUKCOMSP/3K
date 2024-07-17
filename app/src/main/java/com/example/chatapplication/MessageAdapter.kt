package com.example.chatapplication

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class MessageAdapter {

    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sendMessage: Text = itemView.findViewById(R.id.send_text_message)
    }

    class RecvViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sendMessage: Text = itemView.findViewById(R.id.recv_text_message)
    }
}