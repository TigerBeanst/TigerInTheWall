package com.jakting.shareclean

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import com.jakting.shareclean.utils.setDark
import com.jakting.shareclean.utils.setLang
import moe.shizuku.preference.ListPreference
import moe.shizuku.preference.Preference
import moe.shizuku.preference.PreferenceFragment


class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            val fragment = SettingsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.settings, fragment).commit()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.setting_title)
            //supportActionBar!!.subtitle = "v" + BuildConfig.VERSION_NAME
        }
//        if (!getDarkModeStatus(this)) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    internal class SettingsFragment : PreferenceFragment(),
        OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            preferenceManager.defaultPackages = arrayOf(BuildConfig.APPLICATION_ID + ".")
            preferenceManager.sharedPreferencesName = "settings"
            preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
            setPreferencesFromResource(R.xml.settings, null)

            //暗色模式
            val darkPreference = findPreference("drop_dark") as ListPreference
            if (darkPreference.value == null) {
                darkPreference.setValueIndex(0)
            }
            darkPreference.onPreferenceChangeListener = this
            //多语言
            val langPreference = findPreference("drop_lang") as ListPreference
            if (langPreference.value == null) {
                langPreference.setValueIndex(0)
            }
            langPreference.onPreferenceChangeListener = this
        }

        override fun onCreateItemDecoration(): DividerDecoration {
            return CategoryDivideDividerDecoration()
            //return new DefaultDividerDecoration();
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences,
            key: String
        ) {
            //logd("onSharedPreferenceChanged $key")
            val sp = activity!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
            when (key) {
                "drop_dark" -> setDark(sp)
                "drop_lang" -> setLang()
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
//            logd(
//                getString(
//                    R.string.on_preference_change_toast_message,
//                    preference.key,
//                    newValue.toString()
//                )
//            )
            return true
        }
    }


}