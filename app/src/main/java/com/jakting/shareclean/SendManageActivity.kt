package com.jakting.shareclean

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakting.shareclean.adapter.AppsAdapter
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_apps.*


open class SendManageActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var recyclerView: RecyclerView? = null
    private var mSwipeLayout: SwipeRefreshLayout? = null
    private var adapterA: RecyclerView.Adapter<*>? = null
    private var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    lateinit var sp: SharedPreferences
    lateinit var spe: SharedPreferences.Editor
    private var isShowSystemApp = false
    private var isDisableDirectShare = false
    var map: MutableMap<String, Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getDarkModeStatus(this)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_apps)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.app_manage_card_title)
            //supportActionBar!!.subtitle = "v" + BuildConfig.VERSION_NAME
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mSwipeLayout = findViewById(R.id.swipe_layout)
        (mSwipeLayout as SwipeRefreshLayout).setOnRefreshListener(this)
        (mSwipeLayout as SwipeRefreshLayout).setColorSchemeResources(
            R.color.colorAccent,
            R.color.colorPrimary
        )
        sp = this.getSharedPreferences("data", Context.MODE_PRIVATE)
        spe = this.getSharedPreferences("data", Context.MODE_PRIVATE).edit()

        recyclerViewLayoutManager = GridLayoutManager(this@SendManageActivity, 1)
        recyclerView!!.layoutManager = recyclerViewLayoutManager
        clearIFW()
    }

    override fun onRefresh() {
        Handler().postDelayed({
            init()
            mSwipeLayout?.isRefreshing = false
        }, 1000)
    }

    private fun clearIFW() {
        if (Shell.su("rm -f $ifw_file_path").exec().isSuccess) {
            mSwipeLayout?.post {
                mSwipeLayout?.isRefreshing = true
            }
            onRefresh()
            recyclerView?.sbarin(getString(R.string.appmanage_start))
                ?.setAction(getString(R.string.dialog_positive)) {}?.show()
        }
    }

    private fun init() {
        //滑动隐藏 FAB
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && floating_action_button.visibility == View.VISIBLE) {
                    floating_action_button.hide()
                } else if (dy < 0 && floating_action_button.visibility != View.VISIBLE) {
                    floating_action_button.show()
                }
            }
        })
        val apkInfoExtractor = ApkInfoSend(this@SendManageActivity)
        adapterA = AppsAdapter(
            this@SendManageActivity,
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_all_menu -> {
                map = (adapterA as AppsAdapter).map
                (map as MutableMap<String, Boolean>).entries.forEach {
                    (map as MutableMap<String, Boolean>)[it.key] = true
                }
                (adapterA as AppsAdapter).notifyDataSetChanged()
                //finish()
                true
            }
            R.id.clear_menu -> {
                map = (adapterA as AppsAdapter).map
                (map as MutableMap<String, Boolean>).entries.forEach {
                    (map as MutableMap<String, Boolean>)[it.key] = false
                }
                (adapterA as AppsAdapter).notifyDataSetChanged()
                true
            }
            R.id.display_system_menu -> {
                if (item.isChecked) {
                    // If item already checked then unchecked it
                    item.isChecked = false
                    isShowSystemApp = false
                    toast("此时状态：" + item.isChecked)
                } else {
                    // If item is unchecked then checked it
                    item.isChecked = true
                    isShowSystemApp = true
                    toast("此时状态：" + item.isChecked)
                }
                clearIFW()

                true
            }
//            R.id.disable_direct_menu -> {
//                if (item.isChecked) {
//                    // If item already checked then unchecked it
//                    item.isChecked = false
//                    isDisableDirectShare = false
//                } else {
//                    // If item is unchecked then checked it
//                    item.isChecked = true
//                    isDisableDirectShare = true
//                }
//                true
//            }
//            R.id.magisk_menu -> {
//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.menu_magisk))
//                    .setMessage(getString(R.string.menu_magisk_msg))
//                    .setPositiveButton(getString(R.string.menu_magisk_core)) { dialog, which ->
//                        val uri: Uri =
//                            Uri.parse("https://github.com/RikkaApps/Riru/releases/latest")
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                    }
//                    .setNegativeButton(getString(R.string.menu_magisk_ifwenhance)) { dialog, which ->
//                        val uri: Uri =
//                            Uri.parse("https://github.com/Kr328/Riru-IFWEnhance/releases/latest")
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                    }
//                    .show()
//                true
//            }
//            R.id.about_menu -> {
//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.menu_developer))
//                    .setMessage(getString(R.string.menu_about_dialog))
//                    .setPositiveButton(getString(R.string.menu_about_blog)) { dialog, which ->
//                        val uri: Uri = Uri.parse("https://jakting.com")
//                        val intent = Intent(Intent.ACTION_VIEW, uri)
//                        startActivity(intent)
//                    }
//                    .show()
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
