package com.example.chatapplication.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mountainsRef: StorageReference

    var storage = Firebase.storage
    val storageRef = storage.getReference("image/background")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        var uri = Uri.EMPTY
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mountainsRef = storageRef.child("$uid.png")

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
                if(bitmap != null) {
                    uri = it.data?.data
                    mountainsRef.putFile(uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val ListPreferences: ListPreference? = findPreference("font_style")
        ListPreferences?.summaryProvider =
            Preference.SummaryProvider<ListPreference> { preference ->
                val text = preference.entry
                "현재 글자 폰트: $text"
            }

        ListPreferences!!.setOnPreferenceChangeListener { _, newValue ->
            val sharedPref = requireContext().getSharedPreferences("com.example.chatapplication_preferences", Context.MODE_PRIVATE)
            val uid = sharedPref.getString("uid", "")
            val map: Map<String, String> = mapOf("font_style" to newValue.toString())
            val mDbRef = FirebaseDatabase.getInstance().reference
            mDbRef.child("user").child(uid.toString()).child("font").updateChildren(map)
            true
        }

        val editPreferences: EditTextPreference? = findPreference("font_size")
        editPreferences?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text))
                    "현재 기본 사이즈로 설정되어있습니다."
                else
                    "현재 글자 사이즈: $text"
            }

        editPreferences!!.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue.toString().toInt()
            if(30 < value || value < 8) {
                Toast.makeText(requireContext(), "8~30 사이의 값만 가능합니다.", Toast.LENGTH_SHORT).show()
                false
            }
            else {
                val sharedPref = requireContext().getSharedPreferences("com.example.chatapplication_preferences", Context.MODE_PRIVATE)
                val uid = sharedPref.getString("uid", "")
                val map: Map<String, Int> = mapOf("font_size" to value)
                val mDbRef = FirebaseDatabase.getInstance().reference
                mDbRef.child("user").child(uid.toString()).child("font").updateChildren(map)
                true
            }
        }

        val chatImagePreference: Preference? = findPreference("chat_img")
        chatImagePreference?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            reqGalleryLauncher.launch(intent)
            true
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {}

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }


    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
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

}