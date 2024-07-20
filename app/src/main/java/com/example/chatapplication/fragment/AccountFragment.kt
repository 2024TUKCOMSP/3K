package com.example.chatapplication.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentAccountBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AccountFragment : Fragment() {
    lateinit var binding:FragmentAccountBinding
    lateinit var mDbRef: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storage = Firebase.storage
        sharedPref = requireContext().getSharedPreferences("com.example.chatapplication_preferences", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("uid", "")
        mDbRef = FirebaseDatabase.getInstance().reference
        binding = FragmentAccountBinding.inflate(layoutInflater)
        binding.accountfragmentImage.clipToOutline = true

        nameDownload(uid!!)
        imageDownload(uid)

        binding.accountfragmentBtn.setOnClickListener {
            if(binding.accountfragmentName.text.isEmpty())
                Toast.makeText(requireContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                var name = binding.accountfragmentName.text.toString()
                val map: Map<String, String> = mapOf("name" to name)
                mDbRef.child("user").child(uid).updateChildren(map)
            }
        }
        return binding.root
    }

    private fun nameDownload(uid: String) {
        mDbRef.child("user").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(shot in snapshot.children) {
                    if(shot.key == "name")
                        binding.accountfragmentName.setText(shot.value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun imageDownload(uid: String?) {
        val storageRef = storage.getReference("image")
        val mountainsRef = storageRef.child("$uid.png")

        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            Glide.with(requireContext()).load(uri).into(binding.accountfragmentImage)
        }.addOnFailureListener {
        }
    }
}