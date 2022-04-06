package com.jakting.shareclean.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.jakting.shareclean.R
import com.jakting.shareclean.data.AppDetail
import com.jakting.shareclean.utils.MyApplication.Companion.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun getAppDetail(packageName: String): AppDetail {
    //根据包名寻找应用名
    val appDetail = AppDetail()
    val packageInfo: PackageInfo
    val packageManager: PackageManager = appContext.packageManager
    try {
        packageInfo = packageManager.getPackageInfo(packageName, 0)
        appDetail.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
        appDetail.packageName = packageName
        appDetail.versionName = packageInfo.versionName
        appDetail.versionCode = packageInfo.versionCode.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return appDetail
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