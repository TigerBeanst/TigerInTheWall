package com.jakting.shareclean

import android.annotation.SuppressLint
import com.jakting.shareclean.utils.AppsAdapter
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_apps.*


open class SendManageActivity : BaseManageActivity() {

    @SuppressLint("CommitPrefEdits")
    override fun init() {
        super.init()
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.send_manage_card_title)
        }
        val apkInfoExtractor = ApkInfoSend(this)
        adapterA = AppsAdapter(
            this,
            apkInfoExtractor.getAllInstalledApkInfo(isShowSystemApp)!!
        )
        recyclerView!!.adapter = adapterA
        map = (adapterA as AppsAdapter).map
        (map as MutableMap<String, Boolean>).entries.forEach {
            if (sp.getBoolean(it.key + "/send", false)) {
                (map as MutableMap<String, Boolean>)[it.key] = true
            }
        }
        (adapterA as AppsAdapter).notifyDataSetChanged()
        floating_action_button.setOnClickListener {
            floating_action_button.setImageResource(R.drawable.ic_cached_black_24dp)
            var ifw = "<rules>\n"
            (map as MutableMap<String, Boolean>).entries.forEach {
                //logd(it.key)
                val list = it.key.split('/')
                //logd("list: $list")
                //logd("${list[0]} // ${list[1]}")
                spe.putBoolean("${list[0]}/${list[1]}/send", it.value)
                ifw += String.format(ifw_send_content, list[0], list[1])
            }
            ifw += "</rules>"
            spe.apply()
            if (Shell.su("touch $ifw_send_file_path").exec().isSuccess &&
                Shell.su("echo '$ifw' > $ifw_send_file_path").exec().isSuccess
            ) {
                recyclerView?.sbar(getString(R.string.manage_send_success))?.show()
                floating_action_button.setImageResource(R.drawable.ic_check_black_24dp)
            }
            //toast(getString(R.string.manage_send_success))
            //logd(ifw)
        }
    }

    override fun clearIFW() {
        if (Shell.su("rm -f $ifw_file_path_old")
                .exec().isSuccess && Shell.su("rm -f $ifw_send_file_path").exec().isSuccess
        ) {
            mSwipeLayout?.post {
                mSwipeLayout?.isRefreshing = true
            }
            onRefresh()
            recyclerView?.sbarin(getString(R.string.manage_start))
                ?.setAction(getString(R.string.dialog_positive)) {}?.show()
        }
    }
}
