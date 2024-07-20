package com.example.chatapplication.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.databinding.FragmentAccountBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AccountFragment : Fragment() {
    lateinit var binding:FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        imageDownload()
        binding = FragmentAccountBinding.inflate(layoutInflater)
        binding.accountfragmentImage.clipToOutline = true
        return binding.root
    }

    private fun imageDownload() {
        val storage = Firebase.storage
        val storageRef = storage.getReference("image")
        val sharedPref = requireContext().getSharedPreferences("com.example.chatapplication_preferences", Context.MODE_PRIVATE)
        val uid = sharedPref.getString("uid", "")
        val mountainsRef = storageRef.child("$uid.png")

        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            Glide.with(requireContext()).load(uri).into(binding.accountfragmentImage)
        }.addOnFailureListener {
        }
    }
}