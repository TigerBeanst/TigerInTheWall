package com.jakting.shareclean.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.jakting.shareclean.R
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_apps.*
import java.util.*

const val sc_sp_path = "/data/data/com.jakting.shareclean/shared_prefs/data.xml"
const val ifw_file_path = "/data/system/ifw/RnIntentClean.xml"
const val ifw_direct_share_file_path = "/data/system/ifw/RnIntentClean_direct_share.xml"

const val ifw_send_content =
    "   <activity block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.intent.action.SEND\" />\n" +
            "      <cat name=\"android.intent.category.DEFAULT\" />\n" +
            "      <type name=\"*/*\" />\n" +
            "    </intent-filter>\n" +
            "    <component equals=\"%1\$s/%2\$s\" />\n" +
            "    <or>\n" +
            "      <sender type=\"system\" />\n" +
            "      <not>\n" +
            "        <sender type=\"userId\" />\n" +
            "      </not>\n" +
            "    </or>\n" +
            "  </activity>\n"
const val ifw_view_content =
    "   <activity block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.intent.action.VIEW\" />\n" +
            "      <cat name=\"android.intent.category.DEFAULT\" />\n" +
            "      <type name=\"*/*\" />\n" +
            "    </intent-filter>\n" +
            "    <component equals=\"%1\$s/%2\$s\" />\n" +
            "    <or>\n" +
            "      <sender type=\"system\" />\n" +
            "      <not>\n" +
            "        <sender type=\"userId\" />\n" +
            "      </not>\n" +
            "    </or>\n" +
            "  </activity>\n"
const val ifw_send_content_direct_share =
    "<rules>\n" +
            "  <service block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.service.chooser.ChooserTargetService\" />\n" +
            "    </intent-filter>\n" +
            "  </service>\n" +
            "</rules>\n"

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

fun setAppCenter(sp: SharedPreferences, activity: Activity) {
    if (sp.getBoolean("switch_appcenter", true)) {
        AppCenter.start(
            activity.application, "7c5baeda-9936-430b-a034-15db48a113b7",
            Analytics::class.java, Crashes::class.java
        )
    }
}

fun getDarkModeStatus(context: Context): Boolean {
    val mode: Int =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
}

fun setDirectShare(sp: SharedPreferences, context: Context?) {
    if (Build.VERSION.SDK_INT >= 29) {
        context.toast(context?.getString(R.string.misc_disable_direct_No) as String)
    } else {
        when (sp.getBoolean("switch_disable_direct_share", false)) {
            false -> { //启用
                if (Shell.su("rm -f $ifw_direct_share_file_path").exec().isSuccess) {
                    context.toast(context?.getString(R.string.misc_disable_direct_toastEnable) as String)
                }
            }
            true -> { //禁用
                if (Shell.su("touch $ifw_direct_share_file_path").exec().isSuccess &&
                    Shell.su("echo '$ifw_send_content_direct_share' > $ifw_direct_share_file_path")
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

fun setLang(sp: SharedPreferences, activity: Activity) {
    val conf = activity.resources.configuration
    when (sp.getString("drop_lang", "0")) {
        "0" -> { //跟随系统
            conf.setLocale(conf.locales.get(0))
            logd("设置跟随系统语言" + conf.locales.get(0).toLanguageTag())
        }
        "1" -> { //简体中文
            conf.setLocale(Locale.SIMPLIFIED_CHINESE)
            logd("设置简体中文" + Locale.SIMPLIFIED_CHINESE.toLanguageTag())
        }
        "2" -> { //English
            conf.setLocale(Locale.ENGLISH)
            logd("设置English" + Locale.ENGLISH.toLanguageTag())
        }
    }
    activity.createConfigurationContext(conf)
}