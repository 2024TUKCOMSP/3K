package com.example.chatapplication.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class AccountFragment : Fragment() {
    lateinit var binding: FragmentAccountBinding
    lateinit var mDbRef: DatabaseReference
    lateinit var mountainsRef: StorageReference

    var storage = Firebase.storage
    val storageRef = storage.getReference("image")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = requireContext().getSharedPreferences("com.example.chatapplication_preferences", Context.MODE_PRIVATE)
        mDbRef = FirebaseDatabase.getInstance().reference

        binding = FragmentAccountBinding.inflate(layoutInflater)
        binding.accountfragmentImage.clipToOutline = true
        val uid = sharedPref.getString("uid", "")
        mountainsRef = storageRef.child("$uid.png")

        nameDownload(uid!!)
        imageDownload(uid)


        val reqGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            try {
                val calRatio = calculateInSampleSize(it.data!!.data!!,
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize))
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio

                var inputStream = requireContext().contentResolver.openInputStream(it.data!!.data!!)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()
                if(bitmap != null) binding.accountfragmentImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.accountfragmentImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            reqGalleryLauncher.launch(intent)
        }

        binding.accountfragmentBtn.setOnClickListener {
            if(binding.accountfragmentName.text.isEmpty())
                Toast.makeText(requireContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                var name = binding.accountfragmentName.text.toString()
                val map: Map<String, String> = mapOf("name" to name)
                mDbRef.child("user").child(uid).updateChildren(map)
                Toast.makeText(requireContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun calculateInSampleSize(fileUri: Uri, reqW: Int, reqH: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            var inputStream = requireContext().contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
        } catch (e:Exception) {
            e.printStackTrace()
        }

        val (h:Int, w:Int) = options.run {outWidth to outHeight}
        var inSampleSize = 1
        if(h > reqH || w > reqW) {
            val halfH: Int = h / 2
            val halfW: Int = w / 2

            while(halfH / inSampleSize >= reqH && halfW / inSampleSize >= reqW)
                inSampleSize *= 2
        }
        return inSampleSize
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

        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            Glide.with(requireContext()).load(uri).into(binding.accountfragmentImage)
        }.addOnFailureListener {
        }
    }

    private fun imageDelete(uid: String?) {
        mountainsRef.delete()
    }

    private fun imageUpload(uri: Uri, uid: String?) {
        mountainsRef.putFile(uri)
    }
}