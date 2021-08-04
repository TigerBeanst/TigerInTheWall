package com.jakting.shareclean

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.jakting.shareclean.utils.getSystemLanguage
import com.jakting.shareclean.utils.logd
import com.jakting.shareclean.utils.setFirebase
import com.jakting.shareclean.utils.setLang
import java.util.*

open class BaseActivity : LocalizationActivity() {
    private val localizationDelegate = LocalizationApplicationDelegate()

    companion object {
        lateinit var appContext: Context
        lateinit var settingSharedPreferences: SharedPreferences
        lateinit var intentListSharedPreferences: SharedPreferences
        lateinit var intentListSharedPreferencesEditor: SharedPreferences.Editor
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = this
        settingSharedPreferences = getSharedPreferences("com.jakting.shareclean_preferences", Context.MODE_PRIVATE)
        intentListSharedPreferences = getSharedPreferences("intent_list", Context.MODE_PRIVATE)
        intentListSharedPreferencesEditor = intentListSharedPreferences.edit()
        setLang()
        setFirebase()
    }

    override fun attachBaseContext(base: Context) {
        localizationDelegate.setDefaultLanguage(base, getSystemLanguage())
        logd(Locale.getDefault().toLanguageTag())
        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localizationDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

}