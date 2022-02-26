package com.jakting.shareclean.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.databinding.ActivityMainBinding
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

//        binding.headerMain.settingButton.setOnClickListener { view ->
//            Toast.makeText(this, "wao", Toast.LENGTH_SHORT).show()
//        }
        binding.contentMain.card1Module.cardStatus.setOnClickListener { view ->
            Toast.makeText(this, "wao", Toast.LENGTH_SHORT).show()
        }
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
            //判断 IFW Enchance 是否生效，无论是 Riru 版还是 Zygisk 版
            if (moduleApplyAvailable()) {
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
                    ColorStateList.valueOf(themeColor(R.attr.colorPrimary))
//                binding.contentMain.card1Module.cardStatusDarker.backgroundTintList =
//                    ColorStateList.valueOf(themeColor(R.attr.colorPrimaryContainer))
            } else {
                longtoast("没有应用")
            }


        }
    }
}
