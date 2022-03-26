package com.jakting.shareclean.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.jakting.shareclean.R
import com.topjohnwu.superuser.Shell

const val ifw_app_self_path = "/data/system/ifw/RnIntentClean_self.xml"
const val ifw_send_file_path = "/data/system/ifw/RnIntentClean_send.xml"
const val ifw_send_multi_file_path = "/data/system/ifw/RnIntentClean_send_multi.xml"
const val ifw_view_file_path = "/data/system/ifw/RnIntentClean_view.xml"
const val ifw_text_file_path = "/data/system/ifw/RnIntentClean_text.xml"
const val ifw_browser_file_path = "/data/system/ifw/RnIntentClean_browser.xml"
const val ifw_direct_share_file_path = "/data/system/ifw/RnIntentClean_direct_share.xml"

const val ifw_app_self =
    "<rules>\n" +
            "   <activity block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.intent.action.PROCESS_TEXT\" />\n" +
            "      <cat name=\"android.intent.category.DEFAULT\" />\n" +
            "      <type name=\"*/*\" />\n" +
            "    </intent-filter>\n" +
            "    <component equals=\"com.jakting.shareclean/com.jakting.shareclean.utils.ModuleAvailable\" />\n" +
            "    <or>\n" +
            "      <sender type=\"system\" />\n" +
            "      <not>\n" +
            "        <sender type=\"userId\" />\n" +
            "      </not>\n" +
            "    </or>\n" +
            "  </activity>\n" +
            "</rules>\n"

const val ifw_direct_share =
    "<rules>\n" +
            "  <service block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.service.chooser.ChooserTargetService\" />\n" +
            "    </intent-filter>\n" +
            "  </service>\n" +
            "</rules>\n"

fun getIFWContent(tag: String, intentString: String): String {
    return if (isService(tag)) getIFWServiceContent(intentString) else {
        "   <activity block=\"true\" log=\"true\">\n" +
                "    <intent-filter>\n" +
                "      <action name=\"${getIFWAction(tag)}\" />\n" +
                "      <cat name=\"android.intent.category.DEFAULT\" />\n" +
                isBrowser(tag) +
                "    </intent-filter>\n" +
                "    <component equals=\"$intentString\" />\n" +
                "    <or>\n" +
                "      <sender type=\"system\" />\n" +
                "      <not>\n" +
                "        <sender type=\"userId\" />\n" +
                "      </not>\n" +
                "    </or>\n" +
                "  </activity>\n"
    }
}

fun getIFWServiceContent(intentString: String): String {
    return "  <service block=\"true\" log=\"true\">\n" +
            "    <component equals=\"$intentString\" />\n" +
            "  </service>\n"
}

fun isService(tag: String): Boolean {
//    return (tag == "custom_tabs")
    return false
}

fun isBrowser(tag: String): String {
    return if (tag == "browser") {
        "      <cat name=\"android.intent.category.BROWSABLE\" />\n" +
                "      <scheme name=\"http\" />\n" +
                "      <scheme name=\"https\" />\n"
    } else
        "      <type name=\"*/*\" />\n"
}

fun getIFWPath(tag: String): String {
    return when (tag) {
        "direct_share" -> ifw_direct_share_file_path
        "send" -> ifw_send_file_path
        "send_multi" -> ifw_send_multi_file_path
        "view" -> ifw_view_file_path
        "text" -> ifw_text_file_path
        "browser" -> ifw_browser_file_path
        else -> ""
    }
}

fun getIFWAction(tag: String): String {
    return when (tag) {
        "direct_share" -> "android.service.chooser.ChooserTargetService"
        "send" -> Intent.ACTION_SEND
        "send_multi" -> Intent.ACTION_SEND_MULTIPLE
        "view", "browser" -> Intent.ACTION_VIEW
        "text" -> Intent.ACTION_PROCESS_TEXT
        else -> ""
    }
}

fun clearIFW(tag: String): Boolean {
    return when (tag) {
        "send" -> Shell.su("rm -f $ifw_send_file_path").exec().isSuccess
        "send_multi" -> Shell.su("rm -f $ifw_send_multi_file_path").exec().isSuccess
        "view" -> Shell.su("rm -f $ifw_view_file_path").exec().isSuccess
        "text" -> Shell.su("rm -f $ifw_text_file_path").exec().isSuccess
        "browser" -> Shell.su("rm -f $ifw_browser_file_path").exec().isSuccess
        else -> false
    }
}

fun setDirectShare(sp: SharedPreferences, context: Context?) {
    if (Build.VERSION.SDK_INT >= 29) {
        context.toast(context?.getString(R.string.misc_disable_direct_No) as String)
    } else {
        when (sp.getBoolean("switch_disable_direct_share", false)) {
            false -> { //启用
                if (Shell.su("rm -f ${getIFWPath("direct_share")}").exec().isSuccess) {
                    context.toast(context?.getString(R.string.misc_disable_direct_toastEnable) as String)
                }
            }
            true -> { //禁用
                if (Shell.su("touch ${getIFWPath("direct_share")}").exec().isSuccess &&
                    Shell.su("echo '$ifw_direct_share' > ${getIFWPath("direct_share")}")
                        .exec().isSuccess
                ) {
                    context.toast(context?.getString(R.string.misc_disable_direct_toastDisable) as String)
                }
            }
        }
    }
}