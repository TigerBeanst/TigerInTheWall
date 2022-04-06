package com.jakting.shareclean.data

import java.io.Serializable

data class AppIntent(
    val action: String,
    val component: String,
    val componentName: String,
    val type: String
) : Serializable

data class IntentType(
    var share: Boolean,
    var view: Boolean,
    var text: Boolean,
    var browser: Boolean
) : Serializable

data class App(
    val appName: String = "",
    val packageName: String = "",
    val intentList: ArrayList<AppIntent> = arrayListOf(),
    val isSystem: Boolean = false,
    val hasType: IntentType = IntentType(share = false, view = false, text = false, browser = false)
) : Serializable

data class AppDetail(
    var appName: String = "",
    var packageName: String = "",
    var versionCode: String = "",
    var versionName: String = "",
)
