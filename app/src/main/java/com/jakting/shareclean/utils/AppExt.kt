package com.jakting.shareclean.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.jakting.shareclean.utils.MyApplication.Companion.appContext

fun getAppName(packageName: String): String {
    //根据包名寻找应用名
    var appNameGot = ""
    val applicationInfo: ApplicationInfo
    val packageManager: PackageManager = appContext.packageManager
    try {
        applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        appNameGot = packageManager.getApplicationLabel(applicationInfo) as String
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    //logd("名字 $Name")
    return appNameGot
}