package com.jakting.shareclean

import android.os.Bundle
import com.jakting.shareclean.fragment.MiscFragment
import com.jakting.shareclean.fragment.SettingFragment

class PreferenceActivity : BaseActivity() {
    private val miscFragment = MiscFragment()
    private val settingFragment = SettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(intent.getStringExtra("preference")){
            "misc"->{
                supportFragmentManager.beginTransaction().replace(R.id.preference,miscFragment).commit()
                if (supportActionBar != null) {
                    supportActionBar!!.title = getString(R.string.misc_title)
                }
            }
            "setting"->{
                supportFragmentManager.beginTransaction().replace(R.id.preference,settingFragment).commit()
                if (supportActionBar != null) {
                    supportActionBar!!.title = getString(R.string.setting_title)
                }
            }
        }


    }
}