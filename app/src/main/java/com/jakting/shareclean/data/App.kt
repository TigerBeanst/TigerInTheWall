package com.jakting.shareclean.data

data class AppIntent(
    val action: String,
    val component: String,
    val componentName: String,
    val type: String
)

data class IntentType(
    var share: Boolean,
    var view: Boolean,
    var text: Boolean,
    var browser: Boolean
)

data class App(
    val appName: String,
    val packageName: String,
    val intentList: ArrayList<AppIntent>,
    val hasType: IntentType = IntentType(share = false, view = false, text = false, browser = false)
)
