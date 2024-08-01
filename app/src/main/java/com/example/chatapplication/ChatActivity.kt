package com.example.chatapplication

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapplication.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshots
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import okhttp3.internal.wait
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    lateinit var binding:ActivityChatBinding

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference
    lateinit var mountainsRef: StorageReference

    private lateinit var receiverRoom: String
    private lateinit var senderRoom: String

    private lateinit var messageList: ArrayList<Message>

    lateinit var listner1: ValueEventListener
    lateinit var listner2: ValueEventListener

    var storage = Firebase.storage
    val storageRef = storage.getReference("image/background")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mountainsRef = storageRef.child("$uid.png")

        messageList = ArrayList()

        imageDownload()

        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val font_size : String? = sharedPreferences.getString("font_size", "14")
        val font_style_str : String? = sharedPreferences.getString("font_style", "maruburibold")
        val font_stylr_id = this.resources.getIdentifier(font_style_str, "font", packageName)

        binding.chatActivityRecyclerview.layoutManager = LinearLayoutManager(this)
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList, receiverName, font_size!!.toLong(), font_stylr_id)
        binding.chatActivityRecyclerview.adapter = messageAdapter
        
        val onLayoutChangeListener =
            View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                // 키보드가 올라와 높이가 변함
                if (bottom < oldBottom) {
                    binding.chatActivityRecyclerview.scrollBy(0, oldBottom - bottom)
                }
            }
        binding.chatActivityRecyclerview.addOnLayoutChangeListener(onLayoutChangeListener)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        val senderUid = mAuth.currentUser?.uid

        senderRoom = receiverUid + senderUid

        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = receiverName

        binding.chatActivityButton.setOnClickListener {
            if(!binding.chatActivityEdittext.text.isEmpty()) {
                val message = binding.chatActivityEdittext.text.toString()
                val messageObject = Message(message, senderUid, ServerValue.TIMESTAMP, 1)

                mDbRef.child("chats").child(senderRoom).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom).child("messages").push()
                            .setValue(messageObject)
                    }

                binding.chatActivityEdittext.setText("")
                binding.chatActivityRecyclerview.scrollToPosition(messageList.size-1)
            }
        }

        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshat in snapshot.children) {
                        val message = postSnapshat.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                    binding.chatActivityRecyclerview.scrollToPosition(messageList.size-1)
                }
                override fun onCancelled(error: DatabaseError) {}
            })


        listner1 = mDbRef.child("chats").child(receiverRoom).child("messages")
            .addValueEventListener(readListner(receiverRoom, senderUid))
        listner2 = mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(readListner(senderRoom, senderUid))
    }

    override fun onStop() {
        mDbRef.child("chats").child(receiverRoom).child("messages").removeEventListener(listner1)
        mDbRef.child("chats").child(senderRoom).child("messages").removeEventListener(listner2)
        super.onStop()
    }

    class readListner(val Room: String?, val senderUid: String?): ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val mDbRef = FirebaseDatabase.getInstance().reference
            for(postSnapshat in snapshot.children) {
                if(postSnapshat.child("sendId").value.toString() != senderUid) {
                    val map: Map<String, Int> = mapOf("readIndicator" to 0)
                    mDbRef.child("chats").child(Room!!).child("messages")
                        .child(postSnapshat.key.toString()).updateChildren(map)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    }

    private fun imageDownload() {
        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(binding.chatImg)
        }.addOnFailureListener {
        }
    }
}