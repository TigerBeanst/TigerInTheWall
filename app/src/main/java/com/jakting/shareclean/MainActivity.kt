package com.jakting.shareclean

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakting.shareclean.utils.SystemManager
import com.jakting.shareclean.utils.setAppCenter
import com.jakting.shareclean.utils.setDark
import com.jakting.shareclean.utils.setLang
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils.isValidOutput
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var isWorked = false //模块是否被检测到正常工作

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getDarkModeStatus(this)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_main)
        val sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        setSupportActionBar(findViewById(R.id.toolbar))
        init(sp)
        setModuleStatusCard()
    }

    private fun init(sp:SharedPreferences) {
        setAppCenter(sp,this)
        setDark(sp)
        setLang(sp,this)
        app_manage_card.setOnClickListener(this)
        setting_menu.setOnClickListener(this)
        about_menu.setOnClickListener(this)
    }

    private fun setModuleStatusCard() {
        val pid = android.os.Process.myPid()
        val isRoot: Shell.Result =
            Shell.su("cat /proc/$pid/maps").exec()
        if (isValidOutput(isRoot.out)) {
            //授予了 Root 权限
            val result: Shell.Result =
                Shell.su("cat /proc/$pid/maps | grep libriru_ifw_enhance.so").exec()
            if (result.out.toString() != "[]") {
                //Riru - IFW Enhance 已生效
                riru_status_card.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))
                riru_status_card_title.text = getString(R.string.riru_status_card_exist)
                riru_status_card_desc.text = getString(R.string.riru_status_card_exist_detail)
                riru_status_card_icon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                isWorked = true
            } else {
                //Riru - IFW Enhance 未生效
                riru_status_card.setCardBackgroundColor(resources.getColor(R.color.colorRed))
                riru_status_card_title.text = getString(R.string.riru_status_card_not)
                riru_status_card_desc.text = getString(R.string.riru_status_card_not_detail)
                riru_status_card_icon.setImageResource(R.drawable.ic_round_error_24)
            }
        }
//        else {
//            //未授予 Root 权限 & 未 Root
//        }
    }

    private fun getDarkModeStatus(context: Context): Boolean {
        val mode: Int =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun goAppManage() {
        val intent = Intent(this, AppListActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.app_manage_card -> {
                if (!isWorked) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.app_manage_dialog_title)
                        .setMessage(R.string.app_manage_dialog_desc)
                        .setPositiveButton(R.string.dialog_positive) { dialog, which ->
                            goAppManage()
                        }
                        .show()
                }
            }
            R.id.setting_menu -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.about_menu -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
    }
}