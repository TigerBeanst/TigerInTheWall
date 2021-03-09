package com.jakting.shareclean.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri

class ApkInfo(val context: Context, val tag: String) {
    var intentDataList: ArrayList<AppsAdapter.IntentData> = ArrayList()
    var isService = false

    fun getAllInstalledApkInfo(isShowSystemApp: Boolean): ArrayList<AppsAdapter.IntentData> {
        val resolveInfoList: List<ResolveInfo> = when (tag) {
            "send" -> context.packageManager!!.queryIntentActivities(
                Intent(Intent.ACTION_SEND).setType("*/*"),
                PackageManager.MATCH_ALL
            )
            "send_multi" -> context.packageManager!!.queryIntentActivities(
                Intent(Intent.ACTION_SEND_MULTIPLE).setType("*/*"),
                PackageManager.MATCH_ALL
            )
            "view" -> context.packageManager!!.queryIntentActivities(
                Intent(Intent.ACTION_VIEW).setDataAndType(
                    Uri.parse("content://com.jakting.shareclean.fileprovider/selfile/nofile"),
                    "*/*"
                ),
                PackageManager.MATCH_ALL
            )
            "text" -> context.packageManager!!.queryIntentActivities(
                Intent(Intent.ACTION_PROCESS_TEXT).setType("*/*"),
                PackageManager.MATCH_ALL
            )
            "browser" -> context.packageManager!!.queryIntentActivities(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://ic.into.icu")),
                PackageManager.MATCH_ALL
            )
            else -> context.packageManager!!.queryIntentActivities(
                Intent(Intent.ACTION_SEND).setType("*/*"),
                PackageManager.MATCH_ALL
            )
        }
        for (resolveInfo in resolveInfoList) {
            if (!isService) {
                val activityInfo = resolveInfo.activityInfo
                if (isShowSystemApp || !isSystemPackage(resolveInfo)) {
                    val intentData = AppsAdapter.IntentData(
                        getAppName(activityInfo.packageName),
                        activityInfo.packageName,
                        activityInfo.name,
                        (resolveInfo.loadLabel(context.packageManager!!) as String).replace(
                            "\n",
                            ""
                        ),
                        false
                    )
                    intentDataList.add(intentData)
//                logd(activityInfo.name)
//                logd("获取大名称：" + activityInfo.loadLabel(context.packageManager!!))
//                logd("获取小名称：" + resolveInfo.loadLabel(context.packageManager!!))
                }
            } else {
                val serviceInfo = resolveInfo.serviceInfo
                if (isShowSystemApp || !isSystemPackage(resolveInfo)) {
                    val intentData = AppsAdapter.IntentData(
                        getAppName(serviceInfo.packageName),
                        serviceInfo.packageName,
                        serviceInfo.name,
                        (resolveInfo.loadLabel(context.packageManager!!) as String).replace(
                            "\n",
                            ""
                        ),
                        false
                    )
                    intentDataList.add(intentData)
//                    logd(serviceInfo.name)
//                    logd("获取大名称：" + serviceInfo.loadLabel(context.packageManager!!))
//                    logd("获取小名称：" + resolveInfo.loadLabel(context.packageManager!!))
                }
            }

        }
        for (i in intentDataList) {
            logd(i.toString())
        }
        //logd(ApkActivityPosition.toString())
        return intentDataList
    }

    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return if (!isService) {
            resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } else {
            resolveInfo.serviceInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        }
    }

    private fun getAppName(packageName: String): String {
        //根据包名寻找应用名
        var appNameGot = ""
        val applicationInfo: ApplicationInfo
        val packageManager: PackageManager = context.packageManager
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            appNameGot = packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        //logd("名字 $Name")
        return appNameGot
    }
}