package com.jakting.shareclean.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.jakting.shareclean.BuildConfig
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.Shell


class application : Application() {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(Shell.Builder.create()
            .setFlags(Shell.FLAG_REDIRECT_STDERR)
            .setTimeout(10)
        )
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        firebaseAnalytics = Firebase.analytics
        kv = MMKV.defaultMMKV()!!
        shell = Shell.getShell()
        appContext = applicationContext
        settingSharedPreferences = getSharedPreferences(appContext.packageName+"_preferences", MODE_PRIVATE)
        settingSharedPreferencesEditor = getSharedPreferences(appContext.packageName+"_preferences", MODE_PRIVATE).edit()
    }

    companion object {
        lateinit var appContext: Context
        lateinit var settingSharedPreferences: SharedPreferences
        lateinit var settingSharedPreferencesEditor: SharedPreferences.Editor
        lateinit var kv: MMKV
        lateinit var shell: Shell
        private lateinit var firebaseAnalytics: FirebaseAnalytics
        var chipShare = true
        var chipView = true
        var chipText = true
        var chipBrowser = true
    }
}