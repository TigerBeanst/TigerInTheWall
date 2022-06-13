package com.jakting.shareclean.utils

import android.content.Intent
import com.jakting.shareclean.utils.MyApplication.Companion.appContext
import com.jakting.shareclean.utils.MyApplication.Companion.kv
import java.io.File

const val ifw_app_self_path = "/data/system/ifw/TigerInTheWall_self.xml"
const val ifw_send_file_path = "/data/system/ifw/TigerInTheWall_Intent_send.xml"
const val ifw_send_multi_file_path = "/data/system/ifw/TigerInTheWall_Intent_send_multi.xml"
const val ifw_view_file_path = "/data/system/ifw/TigerInTheWall_Intent_view.xml"
const val ifw_text_file_path = "/data/system/ifw/TigerInTheWall_Intent_text.xml"
const val ifw_browser_file_path = "/data/system/ifw/TigerInTheWall_Intent_browser.xml"
val intentTypeList = arrayListOf("1_share", "2_share_multi", "3_view", "4_text", "5_browser")

var ifw_app_self =
    "<rules>\n" +
            "   <activity block=\"true\" log=\"true\">\n" +
            "    <intent-filter>\n" +
            "      <action name=\"android.intent.action.PROCESS_TEXT\" />\n" +
            "      <cat name=\"android.intent.category.DEFAULT\" />\n" +
            "      <type name=\"*/*\" />\n" +
            "    </intent-filter>\n" +
            "    <component equals=\"${appContext.packageName}/com.jakting.shareclean.utils.ModuleAvailable\" />\n" +
            "    <or>\n" +
            "      <sender type=\"system\" />\n" +
            "      <not>\n" +
            "        <sender type=\"userId\" />\n" +
            "      </not>\n" +
            "    </or>\n" +
            "  </activity>\n" +
            "</rules>\n"

fun getIFWContent(tag: String, intentString: String): String {
    return "   <activity block=\"true\" log=\"true\">\n" +
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
        "1_share" -> ifw_send_file_path
        "2_share_multi" -> ifw_send_multi_file_path
        "3_view" -> ifw_view_file_path
        "4_text" -> ifw_text_file_path
        "5_browser" -> ifw_browser_file_path
        else -> ""
    }
}

fun getIFWAction(tag: String): String {
    return when (tag) {
        "1_share" -> Intent.ACTION_SEND
        "2_share_multi" -> Intent.ACTION_SEND_MULTIPLE
        "3_view", "5_browser" -> Intent.ACTION_VIEW
        "4_text" -> Intent.ACTION_PROCESS_TEXT
        else -> ""
    }
}

fun generateIfwFileContent(intentContentList: MutableList<String>):String {
    var ifwContent = "<rules>\n"
    intentContentList.forEach {
        ifwContent += it
    }
    ifwContent += "</rules>"
    return ifwContent
}

fun writeIfwFiles(): Boolean {
    val intentTypeMap = HashMap<String, MutableList<String>>()
    intentTypeMap["1_share"] = mutableListOf()
    intentTypeMap["2_share_multi"] = mutableListOf()
    intentTypeMap["3_view"] = mutableListOf()
    intentTypeMap["4_text"] = mutableListOf()
    intentTypeMap["5_browser"] = mutableListOf()
    kv.allKeys()?.forEach { itKey ->
        if(kv.decodeBool(itKey)){
            intentTypeList.forEach { itType ->
                if (itKey.startsWith(itType)) {
                    intentTypeMap[itType]?.add(getIFWContent(itType, itKey.replace("$itType/", "")))
                }
            }
        }
    }
    var result = true
    intentTypeList.forEach { itType ->
        if (runShell("touch ${getIFWPath(itType)}").isSuccess &&
            runShell("echo '${generateIfwFileContent(intentTypeMap[itType]!!)}' > ${getIFWPath(itType)}").isSuccess
        ) {
            logd("写入${getIFWPath(itType)}成功")
        } else {
            result = false
        }
    }
    return result
}


fun deleteIfwFiles(type: String): Boolean {
    val sendFile = File(getIFWPath("1_share"))
    if(sendFile.exists()){
        logd("z")
    }
    return if (type == "all") {
        runShell( "find /data/system/ifw/ -name \"TigerInTheWall_Intent*.xml\" -exec rm -rf {} \\; ").isSuccess
    } else {
        runShell("rm -f ${getIFWPath(ifw_send_file_path)}").isSuccess
    }
}