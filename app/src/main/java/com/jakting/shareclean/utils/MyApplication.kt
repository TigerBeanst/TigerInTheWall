package com.jakting.shareclean.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import com.tencent.mmkv.MMKV

class MyApplication : Application() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        kv = MMKV.defaultMMKV()!!
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
        var chipShare = true
        var chipView = true
        var chipText = true
        var chipBrowser = true
    }
}