package com.jakting.shareclean.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.jakting.shareclean.R
import com.jakting.shareclean.utils.ifw_direct_share
import com.jakting.shareclean.utils.ifw_direct_share_file_path
import com.jakting.shareclean.utils.logd
import com.jakting.shareclean.utils.runShell
import rikka.material.preference.MaterialSwitchPreference

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
        }
    }
}