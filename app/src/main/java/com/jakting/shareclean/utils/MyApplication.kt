package com.jakting.shareclean.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import com.jakting.shareclean.BuildConfig
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.Shell


class MyApplication : Application() {

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
        kv = MMKV.defaultMMKV()!!
        shell = Shell.getShell()
        appContext = applicationContext
        settingSharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        settingSharedPreferencesEditor = getSharedPreferences("settings", MODE_PRIVATE).edit()
        intentIconMap = HashMap()
    }

    companion object {
        lateinit var appContext: Context
        lateinit var settingSharedPreferences: SharedPreferences
        lateinit var settingSharedPreferencesEditor: SharedPreferences.Editor
        lateinit var intentIconMap: HashMap<String, Drawable>
        lateinit var kv: MMKV
        lateinit var shell: Shell
        var chipShare = true
        var chipView = true
        var chipText = true
        var chipBrowser = true
    }
}