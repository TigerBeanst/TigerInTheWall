package com.jakting.shareclean.data

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import com.jakting.shareclean.utils.MyApplication.Companion.appContext
import com.jakting.shareclean.utils.getAppDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.*

class AppInfo() {

    suspend fun getAppList(): List<App> = withContext(Dispatchers.IO) {
        val resolveInfoListHashMap: HashMap<String, List<ResolveInfo>> = HashMap()


        resolveInfoListHashMap["share"] = (
                appContext.packageManager!!.queryIntentActivities(
                    Intent(Intent.ACTION_SEND).setType("*/*"),
                    PackageManager.MATCH_ALL
                ))
        resolveInfoListHashMap["share_multi"] = (
                appContext.packageManager!!.queryIntentActivities(
                    Intent(Intent.ACTION_SEND_MULTIPLE).setType("*/*"),
                    PackageManager.MATCH_ALL
                ))
        resolveInfoListHashMap["view"] = (
                appContext.packageManager!!.queryIntentActivities(
                    Intent(Intent.ACTION_VIEW).setDataAndType(
                        Uri.parse("content://com.jakting.shareclean.fileprovider/selfile/nofile"),
                        "*/*"
                    ),
                    PackageManager.MATCH_ALL
                ))
        resolveInfoListHashMap["text"] = (
                appContext.packageManager!!.queryIntentActivities(
                    Intent(Intent.ACTION_PROCESS_TEXT).setType("*/*"),
                    PackageManager.MATCH_ALL
                )
                )
        resolveInfoListHashMap["browser_https"] = (
                appContext.packageManager!!.queryIntentActivities(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://ic.into.icu")),
                    PackageManager.MATCH_ALL
                )
                )
        resolveInfoListHashMap["browser_http"] = (
                appContext.packageManager!!.queryIntentActivities(
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://ic.into.icu")),
                    PackageManager.MATCH_ALL
                )
                )

        val finalList: ArrayList<App> = ArrayList()
        resolveInfoListHashMap.forEach { (key, value) ->
            // Intent 分类： 分享/打开方式/长按文本/浏览器
            value.forEach { resolveInfo ->
                // 其中一类 Intent
                val appOr = finalList.stream().filter { app ->
                    app.packageName == resolveInfo.activityInfo.packageName
                }.findFirst()
                if (appOr.isPresent) {
                    // 如果列表里已经存在这个应用（根据包名判断），则把这个应用的 Intent 添加到这个应用的 Intent 集合中
                    appOr.get().intentList.add(
                        AppIntent(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name,
                            (resolveInfo.loadLabel(appContext.packageManager!!) as String).replace(
                                "\n",
                                ""
                            ),
                            key
                        )
                    )
                    appOr.get().setHasType(key)
                } else {
                    val oneApp = App(
                        getAppDetail(resolveInfo.activityInfo.packageName).appName,
                        resolveInfo.activityInfo.packageName,
                        ArrayList<AppIntent>().apply {
                            add(
                                AppIntent(
                                    resolveInfo.activityInfo.packageName,
                                    resolveInfo.activityInfo.name,
                                    (resolveInfo.loadLabel(appContext.packageManager!!) as String).replace(
                                        "\n",
                                        ""
                                    ),
                                    key
                                )
                            )
                        },
                        isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0)
                    )
                    oneApp.setHasType(key)
                    finalList.add(oneApp)
                }
            }
        }

        class SortName : Comparator<App> {
            val localCompare = Collator.getInstance(Locale.getDefault())
            override fun compare(o1: App?, o2: App?): Int {
                if (localCompare.compare(o1!!.appName, o2!!.appName) > 0) {
                    return 1
                } else if (localCompare.compare(o1.appName, o2.appName) < 0) {
                    return -1
                }
                return 0
            }
        }
        Collections.sort(finalList, SortName())
        finalList
    }

    private fun App.setHasType(key: String) {
        when (key) {
            "share", "share_multi" -> {
                hasType.share = true
            }
            "view" -> {
                hasType.view = true
            }
            "text" -> {
                hasType.text = true
            }
            "browser_https", "browser_http" -> {
                hasType.browser = true
            }
        }
    }
}


