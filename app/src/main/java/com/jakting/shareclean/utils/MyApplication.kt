package com.jakting.shareclean.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable

class MyApplication : Application() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
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
        var chipShare = true
        var chipView = true
        var chipText = true
        var chipBrowser = true
    }
}