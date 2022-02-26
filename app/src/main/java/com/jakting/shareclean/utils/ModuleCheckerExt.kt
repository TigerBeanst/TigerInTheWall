package com.jakting.shareclean.utils

import android.content.Intent
import android.content.pm.PackageManager
import com.jakting.shareclean.utils.MyApplication.Companion.appContext
import java.io.File

fun moduleApplyAvailable(): Boolean {
    //检查模块是否已经生效，无论是 Riru 版还是 Zygisk 版
    val selfIfwFile = File(ifw_app_self_path)
    if (!selfIfwFile.exists()) {
        if (runShell("touch $ifw_app_self_path").isSuccess)
            runShell("echo '$ifw_app_self' > $ifw_app_self_path")
    }
    val resolveInfoList = appContext.packageManager!!.queryIntentActivities(
        Intent(Intent.ACTION_PROCESS_TEXT).setType("*/*"),
        PackageManager.MATCH_ALL
    )
    for (resolveInfo in resolveInfoList) {
        if (resolveInfo.activityInfo.packageName == "com.jakting.shareclean") {
            return false
        }
    }
    return true
}

fun moduleInfo(): Array<String> {
    val shellResult = runShell("cat /dev/riru_*/modules/riru_ifw_enhance@ifw_enhance/version")
    return if (shellResult.isSuccess) { // 是 Riru 版
        arrayOf("Riru", moduleVersion("Riru")[0]!!,moduleVersion("Riru")[1]!!)
    } else { // 是 Zygisk 版
        arrayOf("Zygisk", moduleVersion("Zygisk")[0]!!,moduleVersion("Zygisk")[1]!!)
    }
}

fun moduleVersion(injectIf: String): Array<String?> {
    val shellResult = when(injectIf) {
        "Riru" -> runShell("cat /data/adb/modules/riru_ifw_enhance/module.prop").getPureCat()
        "Zygisk" -> runShell("cat /data/adb/modules/zygisk_ifw_enhance/module.prop").getPureCat()
        else -> ""
    }
    logd(shellResult)
    val version = Regex(".*?version=(.*?),.*?versionCode=(.*?),").find(shellResult)?.groupValues
    return arrayOf(version?.get(1), version?.get(2))
}

//
//fun getIFWEnhanceVersionIfRiru(): String {
//    return runShell("cat /dev/riru_*/modules/riru_ifw_enhance@ifw_enhance/version").getPureCat()
//}
//
//fun getIFWEnhanceVersionNameIfRiru(): String {
//    return runShell("cat /dev/riru_*/modules/riru_ifw_enhance@ifw_enhance/version_name").getPureCat()
//}
//
//fun String.moduleResultAvailableIfRiru(): Boolean {
//    // 如果是 false 的话，Riru 版未安装，意味着是 Zygisk 版
//    return !this.contains("No such file or directory")
//}

