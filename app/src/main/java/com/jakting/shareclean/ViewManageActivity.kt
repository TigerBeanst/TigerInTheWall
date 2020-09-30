package com.jakting.shareclean

import com.jakting.shareclean.utils.AppsAdapter
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_apps.*


open class ViewManageActivity : BaseManageActivity(){
    override fun init() {
        super.init()
        val apkInfoExtractor = ApkInfoView(this)
        adapterA = AppsAdapter(
            this,
            apkInfoExtractor.getAllInstalledApkInfo(isShowSystemApp)!!
        )
        recyclerView!!.adapter = adapterA
        map = (adapterA as AppsAdapter).map
        (map as MutableMap<String, Boolean>).entries.forEach {
            if (sp.getBoolean(it.key, false)) {
                (map as MutableMap<String, Boolean>)[it.key] = true
            }
        }
        (adapterA as AppsAdapter).notifyDataSetChanged()
        floating_action_button.setOnClickListener {
            floating_action_button.setImageResource(R.drawable.ic_cached_black_24dp)
            var ifw = "<rules>\n"
            spe.clear()
            (map as MutableMap<String, Boolean>).entries.forEach {
                //logd(it.key)
                if (it.value) {
                    val list = it.key.split('/')
                    //logd("list: $list")
                    //logd("${list[0]} // ${list[1]}")
                    spe.putBoolean("${list[0]}/${list[1]}", it.value)
                    ifw += String.format(ifw_send_content, list[0], list[1])
                }
            }
            if (isDisableDirectShare) {
                ifw += ifw_send_content_direct_share
            }
            ifw += "</rules>"
            spe.apply()
            if (Shell.su("touch $ifw_file_path").exec().isSuccess &&
                Shell.su("echo '$ifw' > $ifw_file_path").exec().isSuccess
            ) {
                recyclerView?.sbar(getString(R.string.appmanage_ifw_success))?.show()
                floating_action_button.setImageResource(R.drawable.ic_check_black_24dp)
            }
            //toast(getString(R.string.appmanage_ifw_success))
            //logd(ifw)
        }
    }
}
