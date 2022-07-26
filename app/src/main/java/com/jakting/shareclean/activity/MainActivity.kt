package com.jakting.shareclean.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
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
        binding.contentMain.card4List.cardThreeLayout.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
//        if(binding.coordinatorLayout.isTotallyVisible()){
//            toast("好活")
//        }else{
//            toast("不好活")
//        }
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
