package com.jakting.shareclean.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.jakting.shareclean.R
import com.jakting.shareclean.utils.setDark
import com.jakting.shareclean.utils.setLang

class SettingFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_setting, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "drop_dark" -> setDark(sharedPreferences)
            "drop_lang" -> setLang()
        }
    }
}