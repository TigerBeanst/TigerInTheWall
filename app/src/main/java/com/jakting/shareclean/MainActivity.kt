package com.jakting.shareclean

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakting.shareclean.utils.logd
import com.jakting.shareclean.utils.toast
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils.isValidOutput
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setModuleStatusCard()
    }

    private fun setModuleStatusCard() {
        var pid = android.os.Process.myPid()
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
            } else{
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
}