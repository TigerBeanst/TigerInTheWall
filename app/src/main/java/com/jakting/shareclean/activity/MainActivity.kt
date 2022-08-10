package com.jakting.shareclean.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.BuildConfig
import com.jakting.shareclean.R
import com.jakting.shareclean.databinding.ActivityMainBinding
import com.jakting.shareclean.utils.*
import com.jakting.shareclean.utils.application.Companion.shell
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val WRITE_REQUEST_CODE = 43
    private val READ_REQUEST_CODE = 42
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            binding.appSelfIcon.setImageDrawable(
                getAppIconByPackageName(
                    this@MainActivity,
                    BuildConfig.APPLICATION_ID
                )
            )
        }

        checkStatus()
        binding.contentMain.card2ManageClean.cardManager.setOnClickListener { view ->
            startActivity(Intent(this, CleanManagerActivity::class.java))
        }
        binding.contentMain.card3ManageIntent.cardManager.setOnClickListener {
            startActivity(Intent(this, CleanManagerActivity::class.java))
        }

        binding.contentMain.card4List.cardList1.setOnClickListener {}
        binding.contentMain.card4List.cardList2.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.misc_backup_and_restore_title)
                .setMessage(R.string.misc_backup_and_restore_msg)
                .setPositiveButton(R.string.misc_backup_and_restore_backup) { _, _ ->
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/json"
                        val time = System.currentTimeMillis() //用于 备份&恢复 的时间戳
                        putExtra(Intent.EXTRA_TITLE, "TigerInTheWall_backup_$time.json")
                    }
                    startActivityForResult(intent, WRITE_REQUEST_CODE)
                }
                .setNegativeButton(R.string.misc_backup_and_restore_restore) { _, _ ->
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }
                    startActivityForResult(intent, READ_REQUEST_CODE)
                }
                .show()
        }
        binding.contentMain.card4List.cardList3.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.contentMain.card4List.cardList4.setOnClickListener {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        toast(getString(R.string.please_wait))
        if (requestCode == WRITE_REQUEST_CODE && resultData != null && resultData.data != null) {
            //备份
            if (backupTIW(resultData.data as Uri)) {
                toast(getString(R.string.misc_backup_and_restore_backup_ok))
            } else {
                toast(getString(R.string.misc_backup_and_restore_error))
            }
        }
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
            //还原
            if (restoreTIW(resultData.data as Uri)) {
                if (deleteIfwFiles("all") && writeIfwFiles()) {
                    toast(getString(R.string.misc_backup_and_restore_restore_ok))
                }
                toast(getString(R.string.misc_backup_and_restore_restore_ok))
            } else {
                toast(getString(R.string.misc_backup_and_restore_error))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkStatus() {
        if (shell.isRoot) {
            // 已经授予 root
            if (moduleApplyAvailable()) {
                // 如果模块已生效
                binding.contentMain.card1Module.cardStatusTitle.text =
                    getString(R.string.status_card_exist)
                val injectIf = moduleInfo()
                // 尝试请求 Riru 目录，如果 Riru 可用，则说明 IFW Enchance 是 Riru 版本
                if (injectIf[0].isNotEmpty()) {
                    binding.contentMain.card1Module.cardStatusDesc.text =
                        String.format(
                            getString(R.string.status_card_exist_module),
                            injectIf[1],
                            injectIf[2]
                        )
                    binding.contentMain.card1Module.cardStatusInjectWhich.text = injectIf[0]
                    binding.contentMain.card1Module.cardStatusIcon.setImageResource(R.drawable.ic_twotone_check_circle_24)
//                binding.contentMain.card1Module.cardStatus.backgroundTintList =
//                    backgroundColor(this)
                    binding.contentMain.card1Module.cardStatus.backgroundTintList =
                        ColorStateList.valueOf(getColorFromAttr(R.attr.colorPrimary))
                    var clickCount = 0
                    binding.contentMain.card1Module.cardStatus.setOnClickListener {
                        clickCount++
                        when (clickCount) {
                            5 -> {
                                binding.contentMain.card1Module.cardStatusInjectWhich.text =
                                    injectIf[0] + "🤥"
                            }
                            10 -> {
                                binding.contentMain.card1Module.cardStatusInjectWhich.text =
                                    injectIf[0] + "🤕"
                            }
                            15 -> {
                                binding.contentMain.card1Module.cardStatusInjectWhich.text =
                                    injectIf[0] + "🤡"
                            }
                            20 -> {
                                binding.contentMain.card1Module.cardStatusInjectWhich.text =
                                    injectIf[0] + "👻"
                                toast(getString(R.string.status_card_click))
                                clickCount = 0
                            }
                        }
                    }
                }
            } else {
                binding.contentMain.card1Module.cardStatusTitle.text =
                    getString(R.string.status_card_no_module)
                binding.contentMain.card1Module.cardStatus.setOnClickListener { view ->
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.status_card_dialog_no_module_title))
                        .setMessage(getString(R.string.status_card_dialog_no_module_content))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.status_card_dialog_more)) { dialog, _ ->
                            openLink(getString(R.string.status_card_dialog_more_url))
                        }
                        .show()
                }
            }


        } else {
            //没有授予 root 的时候，点击卡片会弹窗
            binding.contentMain.card1Module.cardStatus.setOnClickListener { view ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.status_card_dialog_title))
                    .setMessage(getString(R.string.status_card_dialog_content))
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.status_card_dialog_more)) { dialog, _ ->
                        openLink(getString(R.string.status_card_dialog_more_url))
                    }
                    .show()
            }
        }
    }
}
