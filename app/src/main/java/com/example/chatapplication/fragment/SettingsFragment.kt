package com.example.chatapplication.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.chatapplication.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val sizePreferences: ListPreference? = findPreference("font_size")
        sizePreferences?.summaryProvider =
            Preference.SummaryProvider<ListPreference> { preference ->
                val value = preference.value
                if (value.isEmpty())
                    "현재 기본 사이즈로 설정되어있습니다."
                else
                    "현재 글자 사이즈: $value"
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