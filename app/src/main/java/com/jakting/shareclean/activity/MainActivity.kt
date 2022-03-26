package com.jakting.shareclean.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.databinding.ActivityMainBinding
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

        checkStatus()
        binding.contentMain.card2ManageClean.cardManager.setOnClickListener { view ->
            startActivity(Intent(this, IntentManagerActivity::class.java))
        }
        binding.contentMain.card3ManageIntent.cardManager.setOnClickListener {
            startActivity(Intent(this, IntentManagerActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkStatus() {
        if (Shell.rootAccess()) {
            // 已经授予 root
            if (moduleApplyAvailable()) {
                // 如果模块已生效
                binding.contentMain.card1Module.cardStatusTitle.text =
                    getString(R.string.status_card_exist)
                val injectIf = moduleInfo()
                // 尝试请求 Riru 目录，如果 Riru 可用，则说明 IFW Enchance 是 Riru 版本
                binding.contentMain.card1Module.cardStatusDesc.text =
                    String.format(
                        getString(R.string.status_card_exist_module),
                        injectIf[1],
                        injectIf[2]
                    )
                binding.contentMain.card1Module.cardStatusInjectWhich.text = injectIf[0]
                binding.contentMain.card1Module.cardStatusIcon.setImageResource(R.drawable.ic_twotone_check_circle_24)
                binding.contentMain.card1Module.cardStatus.backgroundTintList =
                    backgroundColor(R.color.colorPrimary)
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
            } else {
                longtoast("没有应用")
            }


        } else {
            //没有授予 root 的时候，点击卡片会弹窗
            binding.contentMain.card1Module.cardStatus.setOnClickListener { view ->
                (mdDialog(
                    getString(R.string.status_card_dialog_title),
                    getString(R.string.status_card_dialog_content),
                    "dialog_unknown"
                ).setPositiveButton(
                    getString(R.string.ok), R.drawable.ic_twotone_check_24
                ) { dialog, _ ->
                    dialog.dismiss()
                }.setNegativeButton(
                    getString(R.string.status_card_dialog_more),
                    R.drawable.ic_twotone_open_in_browser_24
                ) { _, _ ->
                    openLink(getString(R.string.status_card_dialog_more_url))
                } as BottomSheetMaterialDialog.Builder).show(290)
            }
        }
    }
}
