package com.jakting.shareclean.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import com.akexorcist.localizationactivity.core.LanguageSetting.setLanguage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakting.shareclean.BaseActivity.Companion.appContext
import com.jakting.shareclean.BaseActivity.Companion.settingSharedPreferences
import com.jakting.shareclean.R
import com.topjohnwu.superuser.Shell
import java.util.*


const val ifw_file_path_old = "/data/system/ifw/RnShareClean.xml"
const val ifw_send_file_path = "/data/system/ifw/RnIntentClean_send.xml"
const val ifw_send_multi_file_path = "/data/system/ifw/RnIntentClean_send_multi.xml"
const val ifw_view_file_path = "/data/system/ifw/RnIntentClean_view.xml"
const val ifw_text_file_path = "/data/system/ifw/RnIntentClean_text.xml"
const val ifw_browser_file_path = "/data/system/ifw/RnIntentClean_browser.xml"
const val ifw_direct_share_file_path = "/data/system/ifw/RnIntentClean_direct_share.xml"

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
                "      <scheme name=\"http\" />\n"
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

fun getManageTypeTitle(tag: String): String {
    return when (tag) {
        "send" -> appContext.getString(R.string.send_title)
        "send_multi" -> appContext.getString(R.string.send_multi_title)
        "view" -> appContext.getString(R.string.view_title)
        "text" -> appContext.getString(R.string.text_title)
        "browser" -> appContext.getString(R.string.browser_title)
        else -> appContext.getString(R.string.app_name)
    }
}

fun clearIFW(tag: String): Boolean {
    return when (tag) {
        "send" -> Shell.su("rm -f $ifw_file_path_old")
            .exec().isSuccess && Shell.su("rm -f $ifw_send_file_path").exec().isSuccess
        "send_multi" -> Shell.su("rm -f $ifw_file_path_old")
            .exec().isSuccess && Shell.su("rm -f $ifw_send_multi_file_path").exec().isSuccess
        "view" -> Shell.su("rm -f $ifw_file_path_old")
            .exec().isSuccess && Shell.su("rm -f $ifw_view_file_path").exec().isSuccess
        "text" -> Shell.su("rm -f $ifw_file_path_old")
            .exec().isSuccess && Shell.su("rm -f $ifw_text_file_path").exec().isSuccess
        "browser" -> Shell.su("rm -f $ifw_file_path_old")
            .exec().isSuccess && Shell.su("rm -f $ifw_browser_file_path").exec().isSuccess
        else -> false
    }
}


fun logd(message: String) =
    Log.d("hjt", message)

fun Context?.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context?.longtoast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun View.sbar(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)

fun View.sbarlong(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)

fun View.sbarin(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)

fun isRoot(): Boolean {
    return Shell.su("command -v su >/dev/null").exec().isSuccess
}

fun String.getPureCat(): String {
    return this.replace("[", "").replace("]", "")
}

fun getRiruRandom(): String {
    return Shell.su("cat /data/adb/riru/dev_random").exec()
        .out.toString().getPureCat()
}

fun getRiruVersion(riruRandom: String): String {
    return Shell.su("cat cat /dev/riru_$riruRandom/version").exec()
        .out.toString().getPureCat()
}

fun getRiruVersionName(riruRandom: String): String {
    return Shell.su("cat /dev/riru_$riruRandom/version_name").exec()
        .out.toString().getPureCat()
}

fun getIFWEnhanceVersion(riruRandom: String): String {
    return Shell.su("cat /dev/riru_$riruRandom/modules/ifw_enhance/version").exec()
        .out.toString().getPureCat()
}

fun getIFWEnhanceVersionName(riruRandom: String): String {
    return Shell.su("cat /dev/riru_$riruRandom/modules/ifw_enhance/version_name").exec()
        .out.toString().getPureCat()
}

fun Context?.getAppIconByPackageName(ApkTempSendActivityName: String): Drawable? {
    val drawable: Drawable?
    drawable = try {
        this?.packageManager?.getApplicationIcon(ApkTempSendActivityName)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        this?.packageManager?.let { ContextCompat.getDrawable(this, R.mipmap.ic_launcher) }
    }
    return drawable
}

fun setFirebase() {
    val isUseFirebase = settingSharedPreferences.getBoolean("switch_firebase", true)
    FirebaseAnalytics.getInstance(appContext).setAnalyticsCollectionEnabled(isUseFirebase)
    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(isUseFirebase)
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

fun setDark(sp: SharedPreferences) {
    when (sp.getString("drop_dark", "0")) {
        "0" -> { //跟随系统
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        "1" -> { //总是开启
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        "2" -> { //总是关闭
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}

fun getSystemLanguage(): Locale {
    return ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
}

fun setLang() {
    when (settingSharedPreferences.getString("drop_lang", "0")) {
        "0" -> { //跟随系统
            setLanguage(appContext, getSystemLanguage())
            logd("设置跟随系统语言")
        }
        "1" -> { //English
            setLanguage(appContext, Locale.ENGLISH)
        }
        "2" -> { //简体中文（大陆）
            setLanguage(appContext, Locale("zh", "CN"))
        }
        "3" -> { //繁體中文（臺灣）
            setLanguage(appContext, Locale("zh", "TW"))
        }
    }
}

fun dip2px(dpValue: Int): Int {
    val dp = dpValue.toFloat()
    val scale = appContext.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

