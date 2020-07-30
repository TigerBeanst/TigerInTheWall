package com.jakting.shareclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jakting.shareclean.utils.logd
import moe.shizuku.preference.*
import java.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            val fragment = SettingsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.settings, fragment).commit()
        }

        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.setting_title)
            //supportActionBar!!.subtitle = "v" + BuildConfig.VERSION_NAME
        }
        if (!getDarkModeStatus(this)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getDarkModeStatus(context: Context): Boolean {
        val mode: Int =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_night_mode -> {
                AppCompatDelegate.setDefaultNightMode(if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)
                recreate()
                return true
            }
            R.id.rtl -> {
                val resources: Resources = baseContext.resources
                val locale = if (resources.getConfiguration()
                        .getLayoutDirection() === View.LAYOUT_DIRECTION_RTL
                ) Locale.ENGLISH else Locale("ar")
                Locale.setDefault(locale)
                resources.getConfiguration().setLocale(locale)
                resources.updateConfiguration(
                    resources.getConfiguration(),
                    resources.getDisplayMetrics()
                )
                startActivity(Intent(this, this.javaClass))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal class SettingsFragment : PreferenceFragment(),
        OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            preferenceManager.defaultPackages = arrayOf(BuildConfig.APPLICATION_ID + ".")
            preferenceManager.sharedPreferencesName = "settings"
            preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
            setPreferencesFromResource(R.xml.settings, null)

            //暗色模式
            var listPreference = findPreference("drop_dark") as ListPreference
            if (listPreference.value == null) {
                listPreference.setValueIndex(0)
            }
            listPreference.onPreferenceChangeListener = this
            //多语言
            listPreference = findPreference("drop_lang") as ListPreference
            if (listPreference.value == null) {
                listPreference.setValueIndex(0)
            }
            listPreference.onPreferenceChangeListener = this
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
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            logd(getString(R.string.on_preference_change_toast_message,preference.key,newValue.toString()))
            return true
        }
    }


}