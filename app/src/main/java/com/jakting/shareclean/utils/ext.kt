package com.jakting.shareclean.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.jakting.shareclean.utils.SystemManager.RootCommand

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