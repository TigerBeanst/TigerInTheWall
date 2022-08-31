package com.jakting.shareclean.data

import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable
import java.io.Serializable

data class AppIntent(
    val packageName: String,
    val component: String,
    val componentName: String,
    var checked: Boolean = false,
    val type: String,
    var icon:Drawable? = null
) : Serializable, BaseObservable()

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

@kotlinx.serialization.Serializable
data class ComponentItem(
    val component: String,
    val status: Boolean
)

@kotlinx.serialization.Serializable
data class BackupEntity(
    val settings: Map<String, String>,
    val components: MutableList<ComponentItem>
)

