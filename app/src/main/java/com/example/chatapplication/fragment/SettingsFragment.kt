package com.example.chatapplication.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.chatapplication.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val sizePreferences: EditTextPreference? = findPreference("font_size")
        sizePreferences?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                val text = preference.text
                if (TextUtils.isEmpty(text))
                    "현재 기본 사이즈로 설정되어있습니다."
                else
                    "현재 글자 사이즈: $text"
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }


    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }
}