package com.jakting.shareclean.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.jakting.shareclean.R
import com.jakting.shareclean.utils.MyApplication.Companion.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

suspend fun getAppIconByPackageName(context: Context, ApkTempSendActivityName: String): Drawable =
    withContext(
        Dispatchers.IO
    ) {
        val drawable: Drawable? = try {
            context.packageManager?.getApplicationIcon(ApkTempSendActivityName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            context.packageManager?.let { ContextCompat.getDrawable(context, R.mipmap.ic_launcher) }
        }
        drawable!!
    }


private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
    return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}