package com.jakting.shareclean

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var isWorked = false //标志 - 模块是否被检测到正常工作
    lateinit var sp: SharedPreferences
    lateinit var spIntent: SharedPreferences
    lateinit var speIntent: SharedPreferences.Editor
    private val WRITE_REQUEST_CODE: Int = 43
    private val READ_REQUEST_CODE: Int = 42

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getDarkModeStatus(this)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        spIntent = getSharedPreferences("intent_list", Context.MODE_PRIVATE)
        speIntent = getSharedPreferences("intent_list", Context.MODE_PRIVATE).edit()
        setSupportActionBar(findViewById(R.id.toolbar))
        init(sp)
        setModuleStatusCard()
    }

    private fun init(sp: SharedPreferences) {
        setAppCenter(sp, this)
        setDark(sp)
        //setLang(sp,this)
        riru_status_card.setOnClickListener(this)
        send_manage_card.setOnClickListener(this)
        view_card.setOnClickListener(this)
        text_card.setOnClickListener(this)
        backup_menu.setOnClickListener(this)
        misc_menu.setOnClickListener(this)
        setting_menu.setOnClickListener(this)
        about_menu.setOnClickListener(this)
    }

    private fun setModuleStatusCard() {
        val pid = android.os.Process.myPid()
        if (isRoot()) {
            //授予了 Root 权限
            val result: Shell.Result =
                Shell.su("cat /proc/$pid/maps | grep libriru_ifw_enhance.so").exec()
            if (result.out.toString() != "[]") {
                //Riru - IFW Enhance 已生效
                riru_status_card.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))
                riru_status_card_title.text = getString(R.string.riru_status_card_exist)
                riru_status_card_desc.text = getString(R.string.riru_status_card_exist_detail)
                riru_status_card_icon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                send_manage_card.setCardBackgroundColor(resources.getColor(R.color.colorGreen1))
                view_card.setCardBackgroundColor(resources.getColor(R.color.colorGreen2))
                text_card.setCardBackgroundColor(resources.getColor(R.color.colorGreen3))
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

    private fun startSendManage() {
        val intent = Intent(this, SendManageActivity::class.java)
        startActivity(intent)
    }

    private fun startViewManage() {
        val intent = Intent(this, ViewManageActivity::class.java)
        startActivity(intent)
    }

    private fun startTextManage() {
        val intent = Intent(this, TextManageActivity::class.java)
        startActivity(intent)
    }

    private fun clickBackupRestore() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.backup_title))
            .setMessage(getString(R.string.br_msg))
            .setNegativeButton(resources.getString(R.string.br_restore)) { _, _ ->
                //还原
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
                startActivityForResult(intent, READ_REQUEST_CODE)
            }
            .setPositiveButton(resources.getString(R.string.br_backup)) { _, _ ->
                //备份
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    val time = System.currentTimeMillis() //用于 备份&恢复 的时间戳
                    putExtra(Intent.EXTRA_TITLE, "RnIntentClean_Backup_$time")
                }
                startActivityForResult(intent, WRITE_REQUEST_CODE)
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == WRITE_REQUEST_CODE && resultData != null && resultData.data != null) {
            //备份
            if (backupSharedPreferences(resultData.data as Uri)) {
                toast(getString(R.string.br_backup_ok))
            } else {
                toast(getString(R.string.br_backup_error))
            }
        }
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
            //还原
            if (restoreSharedPreferences(resultData.data as Uri)) {
                toast(getString(R.string.br_restore_ok))
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.riru_status_card -> {
                if (!isWorked) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.manage_dialog_title)
                        .setMessage(R.string.riru_status_card_download)
                        .setPositiveButton(R.string.riru_status_card_download_btn) { _, _ ->
                            val uri = Uri.parse("https://sc.into.icu")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                        .show()
                }
            }
            R.id.send_manage_card -> {
                if (!isWorked) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.manage_dialog_title)
                        .setMessage(R.string.manage_dialog_desc)
                        .setPositiveButton(R.string.dialog_positive) { dialog, which ->
                            startSendManage()
                        }
                        .show()
                } else {
                    startSendManage()
                }
            }
            R.id.view_card -> {
                if (!isWorked) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.manage_dialog_title)
                        .setMessage(R.string.manage_dialog_desc)
                        .setPositiveButton(R.string.dialog_positive) { dialog, which ->
                            startViewManage()
                        }
                        .show()
                } else {
                    startViewManage()
                }
            }
            R.id.text_card -> {
                if (!isWorked) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.manage_dialog_title)
                        .setMessage(R.string.manage_dialog_desc)
                        .setPositiveButton(R.string.dialog_positive) { dialog, which ->
                            startTextManage()
                        }
                        .show()
                } else {
                    startTextManage()
                }
            }
            R.id.backup_menu -> {
                clickBackupRestore()
            }
            R.id.misc_menu -> {
                val intent = Intent(this, MiscActivity::class.java)
                startActivity(intent)
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

    private fun backupSharedPreferences(uri: Uri): Boolean {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    it.write(
                        JSON.toJSONString(spIntent.all).toByteArray()
                    )
                }
            }
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    @Throws(IOException::class)
    private fun restoreSharedPreferences(uri: Uri): Boolean {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        val map = JSON.parseObject(stringBuilder.toString())
        speIntent.clear()
        map.forEach {
            speIntent.putBoolean(it.key, it.value as Boolean)
        }
        speIntent.apply()
        return true
    }
}