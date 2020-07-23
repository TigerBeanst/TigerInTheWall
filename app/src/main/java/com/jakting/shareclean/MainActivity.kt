package com.jakting.shareclean

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakting.shareclean.utils.logd
import com.jakting.shareclean.utils.toast
import com.topjohnwu.superuser.Shell


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        is_RiruIFWEnhance_Installed()
    }

    private fun is_RiruIFWEnhance_Installed(): Boolean {
        var pid = android.os.Process.myPid()
        val result: Shell.Result =
            Shell.su("cat /proc/$pid/maps | grep libriru_ifw_enhance.so").exec()
        if (result.getOut().toString() != "[]") {
            //Riru - IFW Enhance 已生效
            return true
        }
        return false
    }
}