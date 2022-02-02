package com.jakting.shareclean.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import java.io.File

fun moduleApplyAvailable(context: Context): Boolean {
    //检查模块是否已经生效，无论是 Riru 版还是 Zygisk 版
    val selfIfwFile = File(ifw_app_self_path)
    if (!selfIfwFile.exists()) {
        if (runShell("touch $ifw_app_self_path").isSuccess)
            runShell("echo '$ifw_app_self' > $ifw_app_self_path")
    }
    val resolveInfoList = context.packageManager!!.queryIntentActivities(
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

fun moduleIsRiru(): String {
    return if (runShell("cat /dev/riru_*/modules/riru_ifw_enhance@ifw_enhance/version").isSuccess) {
        "Riru"
    } else {
        "Zygisk"
    }
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

