package com.jakting.shareclean.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.jakting.shareclean.R
import java.io.File

class ApkInfoText(context2: Context?) {
    var context1: Context? = context2
    var json: JSONArray = JSONArray()

    fun getAllInstalledApkInfo(isShowSystemApp: Boolean): JSONArray? {
        //val ApkActivityPosition: MutableList<Double> = ArrayList()
        val intent = Intent(Intent.ACTION_PROCESS_TEXT)
        intent.type = "*/*"
        //intent.type = "*/*"
        val resolveInfoList: List<ResolveInfo> =
            context1?.packageManager!!.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )
        for (resolveInfo in resolveInfoList) {
            val activityInfo = resolveInfo.activityInfo
            if (isShowSystemApp || !isSystemPackage(resolveInfo)) {
                val appObject = JSONObject()
                appObject["app_name"] = getAppName(activityInfo.packageName)
                appObject["package_name"] = activityInfo.packageName
                appObject["activity"] = activityInfo.name
                appObject["activity_name"] =
                    (resolveInfo.loadLabel(context1?.packageManager!!) as String).replace("\n", "")
//                if (json.containsKey(activityInfo.packageName)) {
//                    var jsonObj = json.getJSONObject(activityInfo.packageName)
//                    appObject = jsonObj.getJSONArray("app_activity")
//                    appObject.add(i + 0.1)
//                } else {
//                    i++
//                    appObject["app_name"] = activityInfo.loadLabel(context1?.packageManager!!)
//                    ApkActivityPosition.add(i + 0.1)
//                }
//                activityArray.add(activityObject)
//                appObject["app_activity"] = activityArray
                json.add(appObject)
                //println(json.toJSONString())
//                logd(activityInfo.name)
//                logd("获取大名称：" + activityInfo.loadLabel(context1?.packageManager!!))
//                logd("获取小名称：" + resolveInfo.loadLabel(context1?.packageManager!!))
            }
        }
        println(json.toJSONString())
        //logd(ApkActivityPosition.toString())
        return json
    }

    private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun getAppName(PackageName: String): String? {
        //logd("包名 $PackageName")
        var Name = ""
        val applicationInfo: ApplicationInfo
        val packageManager: PackageManager = context1!!.packageManager
        try {
            applicationInfo = packageManager.getApplicationInfo(PackageName, 0)
            Name = packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        //logd("名字 $Name")
        return Name
    }
}