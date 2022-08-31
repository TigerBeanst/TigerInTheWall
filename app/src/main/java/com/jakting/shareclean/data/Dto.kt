package com.jakting.shareclean.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuickCleanRuleEntityListApiResult(
    @SerialName("rules")
    val rules: List<String>
)

data class QuickCleanRule(
    val type: String
)

@Serializable
data class QuickCleanListApiResult(
    @SerialName("rule_id")
    val ruleId: String,
    @SerialName("rule_info")
    val ruleInfo: QuickCleanRuleInfo,
    @SerialName("updateTime")
    val updateTime: Long,
    @SerialName("uploadTime")
    val uploadTime: Long
)

@Serializable
data class QuickCleanRuleInfo(
    @SerialName("default")
    val defaultLang: QuickCleanDefault,
    @SerialName("i18n")
    val i18n: List<QuickCleanI18n>
)

@Serializable
data class QuickCleanDefault(
    @SerialName("rule_desc")
    val ruleDesc: String,
    @SerialName("rule_name")
    val ruleName: String
)

@Serializable
data class QuickCleanI18n(
    @SerialName("rule_desc")
    val ruleDesc: String,
    @SerialName("rule_lang")
    val ruleLang: String,
    @SerialName("rule_name")
    val ruleName: String
)