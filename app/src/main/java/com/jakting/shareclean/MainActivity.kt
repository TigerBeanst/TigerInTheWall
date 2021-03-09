package com.jakting.shareclean

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.fastjson.JSON
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.io.SuFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), View.OnClickListener {

    private var isWorked = false //标志 - 模块是否被检测到正常工作
    private lateinit var riruRandom: String

    companion object {
        private const val WRITE_REQUEST_CODE = 43
        private const val READ_REQUEST_CODE = 42
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.app_name)
        setSupportActionBar(findViewById(R.id.toolbar))
        init()
        setModuleStatusCard()
    }

    private fun init() {
        setDark(settingSharedPreferences)
        //setLang(sp,this)
        riru_status_card.setOnClickListener(this)
        manage_card.setOnClickListener(this)
        backup_menu.setOnClickListener(this)
        misc_menu.setOnClickListener(this)
        setting_menu.setOnClickListener(this)
        about_menu.setOnClickListener(this)
    }

    private fun setModuleStatusCard() {
        if (isRoot()) {
            //授予了 Root 权限
            riruRandom = getRiruRandom()
            logd("riruRandom：$riruRandom")
            if (riruRandom.isNotEmpty()
                && SuFile.open("/dev/riru_$riruRandom/modules/ifw_enhance").exists()
            ) {
                // Riru - Core 已生效 + Riru - IFWEnhance 已生效
                riru_status_card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    )
                )
                riru_status_card_title.text = getString(R.string.riru_status_card_exist)
                riru_status_card_desc.text =
                    String.format(getString(R.string.riru_status_card_exist_detail))
                riru_status_card_icon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                manage_card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorGreen1
                    )
                )
                isWorked = true
            } else {
                riru_status_card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorRed
                    )
                )
                riru_status_card_title.text = getString(R.string.riru_status_card_not)
                riru_status_card_desc.text = getString(R.string.riru_status_card_not_detail)
                riru_status_card_icon.setImageResource(R.drawable.ic_round_error_24)
            }
        }
    }

    private fun showManageDialog() {
        val manageItems = resources.getStringArray(R.array.intent_list)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.manage_card_title))
            .setItems(manageItems) { dialog, which ->
                val tag = when (which) {
                    0 -> "send"
                    1 -> "send_multi"
                    2 -> "view"
                    3 -> "text"
                    4 -> "browser"
                    else -> ""
                }
                val intent = Intent(this, ManageActivity::class.java)
                intent.putExtra("tag", tag)
                startActivity(intent)
            }
            .show()
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
        toast(getString(R.string.please_wait))
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
                            val uri = Uri.parse("https://ic.into.icu")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                        .show()
                } else {
                    val riruVersion = getRiruVersion(riruRandom)
                    val riruVersionName = getRiruVersionName(riruRandom)
                    val ifwVersion = getIFWEnhanceVersion(riruRandom)
                    val ifwVersionName = getIFWEnhanceVersionName(riruRandom)
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.riru_status_card_exist_dialog_title)
                        .setMessage(
                            String.format(
                                getString(R.string.riru_status_card_exist_dialog_msg),
                                riruVersionName,
                                riruVersion,
                                ifwVersionName,
                                ifwVersion
                            )
                        )
                        .show()
                }
            }
            R.id.manage_card -> {
                if (!isWorked) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.manage_dialog_title)
                        .setMessage(R.string.manage_dialog_desc)
                        .setPositiveButton(R.string.dialog_positive) { _, _ ->
                            showManageDialog()
                        }
                        .show()
                } else {
                    showManageDialog()
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
            contentResolver.openFileDescriptor(uri, "w")?.use { fileDescriptor ->
                FileOutputStream(fileDescriptor.fileDescriptor).use {
                    it.write(
                        JSON.toJSONString(intentListSharedPreferences.all).toByteArray()
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
        intentListSharedPreferencesEditor.clear()
        map.forEach {
            intentListSharedPreferencesEditor.putBoolean(it.key, it.value as Boolean)
        }
        intentListSharedPreferencesEditor.apply()
        return true
    }
}