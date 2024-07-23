package com.example.chatapplication.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.chatapplication.R
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

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
}