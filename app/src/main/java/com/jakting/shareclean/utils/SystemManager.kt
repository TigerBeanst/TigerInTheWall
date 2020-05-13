package com.jakting.shareclean.utils

import android.app.Activity
import android.util.Log
import java.io.DataOutputStream


object SystemManager : Activity() {
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
    fun RootCommand(command: String): Boolean {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes(command + "\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: Exception) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.message)
            return false
        } finally {
            try {
                os?.close()
                process!!.destroy()
            } catch (e: Exception) {
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ")
        return true
    }
}