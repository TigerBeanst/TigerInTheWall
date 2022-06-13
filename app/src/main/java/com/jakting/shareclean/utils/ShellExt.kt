package com.jakting.shareclean.utils

import com.topjohnwu.superuser.Shell


fun runShell(cmd: String): Shell.Result {
    return Shell.cmd(cmd).exec()
}

fun Shell.Result.getPureCat(): String {
    return this.out.toString().replace("[", "").replace("]", "")
}