package com.jakting.shareclean.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.databinding.ActivityMainBinding
import com.jakting.shareclean.R
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

        checkStatus()
        binding.contentMain.card2ManageApp.cardManager.setOnClickListener { view ->
            val intent = Intent(this, IntentManagerActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                view,
                "rn_manager_container"
            )
            startActivity(intent, options.toBundle())
        }
        binding.contentMain.card3ManageCategory.cardManager.setOnClickListener { view ->
            val viewww = binding.contentMain.card3ManageCategory.cardManager
//            val viewww = binding.contentMain.card2Manage.cardManager
            val intent = Intent(this, IntentManagerActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                viewww,
                "rn_manager_container"
            )
            startActivity(intent, options.toBundle())
        }
    }

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
                binding.contentMain.card1Module.cardStatus.setOnClickListener { view ->
                    clickCount++
                    when (clickCount) {
                        1 -> toast("🤥🤥🤥🤥🤥")
                        2 -> toast("🤕🤕🤕🤕🤕")
                        3 -> toast("🤡🤡🤡🤡🤡")
                        4 -> {
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
                mdDialog(
                    getString(R.string.status_card_dialog_title),
                    getString(R.string.status_card_dialog_content),
                    rightTitle = getString(R.string.ok),
                    otherTitle = getString(R.string.status_card_dialog_more),
                    onOther = { _, _ ->
                        openLink(getString(R.string.status_card_dialog_more_url))
                    },
                )
            }
        }
    }
}
