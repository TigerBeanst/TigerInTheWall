package com.jakting.shareclean.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.jakting.shareclean.utils.SystemManager.RootCommand
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import java.util.*


val ifw_content =
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
val ifw_content_direct_share =
            "   <service block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.service.chooser.ChooserTargetService\"/>\n" +
            "    </intent-filter>\n" +
            "  </service>\n"
fun logd(message: String) =
    Log.d("debug", message)

fun Context?.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context?.longtoast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun getRoot(packageCodePath:String):Boolean{
    val apkRoot = "chmod 777 $packageCodePath"
    return RootCommand(apkRoot)
}
fun setAppCenter(sp:SharedPreferences,activity: Activity){
    if(sp.getBoolean("switch_appcenter",true)){
        AppCenter.start(
            activity.application, "7c5baeda-9936-430b-a034-15db48a113b7",
            Analytics::class.java, Crashes::class.java
        )
    }
}
fun setDark(sp:SharedPreferences){
    when(sp.getString("drop_dark","0")){
        "0"->{ //跟随系统
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        "1"->{ //总是开启
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        "2"->{ //总是关闭
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
fun setLang(sp:SharedPreferences,activity: Activity){
    val conf = activity.resources.configuration
    when(sp.getString("drop_lang","0")){
        "0"->{ //跟随系统
            conf.setLocale(conf.locales.get(0))
            logd("设置跟随系统语言"+conf.locales.get(0).toLanguageTag())
        }
        "1"->{ //简体中文
            conf.setLocale(Locale.SIMPLIFIED_CHINESE)
            logd("设置简体中文"+Locale.SIMPLIFIED_CHINESE.toLanguageTag())
        }
        "2"->{ //English
            conf.setLocale(Locale.ENGLISH)
            logd("设置English"+Locale.ENGLISH.toLanguageTag())
        }
    }
    activity.createConfigurationContext(conf)
    //activity.recreate()
}