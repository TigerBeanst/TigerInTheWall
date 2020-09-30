package com.jakting.shareclean

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jakting.shareclean.utils.*
import moe.shizuku.preference.*


class MiscActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_misc)
        if (savedInstanceState == null) {
            val fragment = MiscFragment()
            supportFragmentManager.beginTransaction().replace(R.id.misc, fragment).commit()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.misc_title)
            //supportActionBar!!.subtitle = "v" + BuildConfig.VERSION_NAME
        }
        if (!getDarkModeStatus(this)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    internal class MiscFragment : PreferenceFragment(),
        OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            preferenceManager.defaultPackages = arrayOf(BuildConfig.APPLICATION_ID + ".")
            preferenceManager.sharedPreferencesName = "settings"
            preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
            setPreferencesFromResource(R.xml.misc, null)

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
            logd("onSharedPreferenceChanged $key")
            val sp = activity!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
            when (key) {
                "switch_disable_direct_share" -> setDirectShare(sp,context)
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            logd(
                getString(
                    R.string.on_preference_change_toast_message,
                    preference.key,
                    newValue.toString()
                )
            )
            return true
        }
    }


}