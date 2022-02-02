package com.jakting.shareclean.utils

import com.topjohnwu.superuser.Shell
import java.io.BufferedReader
import java.io.InputStreamReader


fun runShell(cmd: String): Shell.Result {
    return Shell.su(cmd).exec()
}

fun Shell.Result.getPureCat(): String {
    return this.out.toString().replace("[", "").replace("]", "")
}