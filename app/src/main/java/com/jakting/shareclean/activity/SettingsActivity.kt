package com.jakting.shareclean.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakting.shareclean.R
import com.jakting.shareclean.utils.*
import com.jakting.shareclean.utils.application.Companion.kv
import com.jakting.shareclean.utils.application.Companion.settingSharedPreferences
import rikka.material.preference.MaterialSwitchPreference
import rikka.preference.SimpleMenuPreference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.theme.applyStyle(
            rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference,
            true
        );
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_setting, rootKey)

            // 直接分享
            val directShareSwitch = findPreference<MaterialSwitchPreference>("pref_direct_share")
            if (directShareSwitch != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    directShareSwitch.isEnabled = false
                } else {
                    directShareSwitch.setOnPreferenceChangeListener { preference, newValue ->
                        if (newValue as Boolean) {
                            if (runShell("touch $ifw_direct_share_file_path").isSuccess &&
                                runShell("echo '$ifw_direct_share' > $ifw_direct_share_file_path").isSuccess
                            ) {
                                activity.logd("写入 $ifw_direct_share_file_path 成功")
                            } else {
                                activity.logd("写入 $ifw_direct_share_file_path 失败")
                            }
                        } else {
                            if (runShell("rm -f $ifw_direct_share_file_path").isSuccess) {
                                activity.logd("删除 $ifw_direct_share_file_path 成功")
                            } else {
                                activity.logd("删除 $ifw_direct_share_file_path 失败")
                            }
                        }
                        true
                    }


                }
            }

            //设置规则镜像
            val mirrorPreference = findPreference<SimpleMenuPreference>("pref_mirrors")
            val customPreference = findPreference<EditTextPreference>("pref_mirrors_custom")
            customPreference?.isVisible =
                settingSharedPreferences.getString("pref_mirrors", "0") == "99"
            customPreference?.summary = settingSharedPreferences.getString("pref_mirrors_custom", "")
            customPreference?.setOnPreferenceChangeListener { preference, newValue ->
                customPreference.summary = newValue as String
                true
            }
            mirrorPreference?.setOnPreferenceChangeListener { preference, newValue ->
                val mirrorName = when (newValue) {
                    "0" -> "Github"
                    "1" -> "jsDelivr"
                    "2" -> "FastGit"
                    "99" -> getString(R.string.setting_rules_mirror_custom)
                    else -> getString(R.string.setting_rules_mirror_custom)
                }
                preference.summary =
                    String.format(getString(R.string.setting_rules_mirror_summary), mirrorName)
                customPreference?.isVisible = newValue == "99"
                true
            }

            //重置 IFW
            findPreference<Preference>("pref_reset_ifw")?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(activity as Context)
                    .setTitle(R.string.setting_reset_secondary_confirmation)
                    .setMessage(R.string.setting_reset_secondary_confirmation_summary)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        if (deleteIfwFiles("all")) {
                            activity.toast(R.string.setting_reset_success, true)
                        } else {
                            activity.toast(R.string.setting_reset_error, true)
                        }
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .show()
                true
            }

            //重置配置
            findPreference<Preference>("pref_reset_configuration")?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(activity as Context)
                    .setTitle(R.string.setting_reset_secondary_confirmation)
                    .setMessage(R.string.setting_reset_secondary_confirmation_summary)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        kv.clearAll()
                        activity.toast(R.string.setting_reset_success, true)
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .show()
                true
            }
        }
    }
}