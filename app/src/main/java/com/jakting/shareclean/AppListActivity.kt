package com.jakting.shareclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakting.shareclean.adapter.AppsAdapter
import com.jakting.shareclean.utils.*
import com.jakting.shareclean.utils.SystemManager.RootCommand
import kotlinx.android.synthetic.main.activity_applist.*
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


open class AppListActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var recyclerView: RecyclerView? = null
    private var adapterA: RecyclerView.Adapter<*>? = null
    private var mSwipeLayout: SwipeRefreshLayout? = null
    private var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    lateinit var sp: SharedPreferences
    lateinit var spe: SharedPreferences.Editor
    private val ifw_file_path = "/data/system/ifw/RnShareClean.xml"
    private var isShowSystemApp = false
    private var isDisableDirectShare = false
    var map: MutableMap<String, Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applist)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mSwipeLayout = findViewById(R.id.swipe_layout)
        (mSwipeLayout as SwipeRefreshLayout).setOnRefreshListener(this)
        (mSwipeLayout as SwipeRefreshLayout).setColorSchemeResources(
            R.color.colorAccent,
            R.color.colorPrimary
        )
        sp = this.getSharedPreferences("data", Context.MODE_PRIVATE)
        spe = this.getSharedPreferences("data", Context.MODE_PRIVATE).edit()

        recyclerViewLayoutManager = GridLayoutManager(this@AppListActivity, 1)
        recyclerView!!.layoutManager = recyclerViewLayoutManager
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.dialog_title))
            .setMessage(resources.getString(R.string.dialog_content))
            .setNegativeButton(resources.getString(R.string.dialog_negative)) { dialog, which ->
                // Respond to negative button press
                finish()
            }
            .setPositiveButton(resources.getString(R.string.dialog_positive)) { dialog, which ->
                // Respond to positive button press
                val apkRoot = "chmod 777 $packageCodePath"
                RootCommand(apkRoot)
                RootCommand("rm -f $ifw_file_path")
                mSwipeLayout?.post {
                    mSwipeLayout?.isRefreshing = true
                }
                onRefresh()
                toast(getString(R.string.after_start))
            }
            .show()
        AppCenter.start(
            application, "7c5baeda-9936-430b-a034-15db48a113b7",
            Analytics::class.java, Crashes::class.java
        )
    }

    override fun onRefresh() {
        logd("这里这里这里这里这里这里这里")
        Handler().postDelayed({
            //Thread.sleep(1000)
            init()
            mSwipeLayout?.isRefreshing = false
            mSwipeLayout?.isEnabled = false
        }, 1000)
    }

    private fun init() {
        var apkInfoExtractor = ApkInfoExtractor(this@AppListActivity)
        adapterA = AppsAdapter(
            this@AppListActivity,
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
            var ifw: String = "<rules>\n"
            spe.clear()
            (map as MutableMap<String, Boolean>).entries.forEach {
                //logd(it.key)
                if (it.value) {
                    val list = it.key.split('/')
                    //logd("list: $list")
                    //logd("${list[0]} // ${list[1]}")
                    spe.putBoolean("${list[0]}/${list[1]}", it.value)
                    ifw += String.format(ifw_content, list[0], list[1])
                }
            }
            if (isDisableDirectShare) {
                ifw += ifw_content_direct_share
            }
            ifw += "</rules>"
            spe.apply()
            //val ifw_file_path = "/sdcard/RnShareClean.xml"
            RootCommand("touch $ifw_file_path")
            RootCommand("echo '$ifw' > $ifw_file_path")
            val toastApply =
                Toast.makeText(this, getString(R.string.ifw_success), Toast.LENGTH_LONG)
            toastApply.setGravity(Gravity.BOTTOM, 0, this.resources.displayMetrics.heightPixels / 8)
            floating_action_button.setImageResource(R.drawable.ic_check_black_24dp)
            toastApply.show()
            //toast(getString(R.string.ifw_success))
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
                    item.isChecked = false;
                    isShowSystemApp = false;
                } else {
                    // If item is unchecked then checked it
                    item.isChecked = true;
                    isShowSystemApp = true;
                }
                if(getRoot(packageCodePath)){
                    RootCommand("rm -f $ifw_file_path")
                    mSwipeLayout?.post {
                        mSwipeLayout?.isRefreshing = true
                    }
                    onRefresh()
                    toast(getString(R.string.after_start))
                }else{
                    toast("没ROOT")
                }

                true
            }
            R.id.disable_direct_menu -> {
                if (item.isChecked) {
                    // If item already checked then unchecked it
                    item.isChecked = false
                    isDisableDirectShare = false
                } else {
                    // If item is unchecked then checked it
                    item.isChecked = true
                    isDisableDirectShare = true
                }
                true
            }
            R.id.magisk_menu -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.menu_magisk))
                    .setMessage(getString(R.string.menu_magisk_msg))
                    .setPositiveButton(getString(R.string.menu_magisk_core)) { dialog, which ->
                        val uri: Uri =
                            Uri.parse("https://github.com/RikkaApps/Riru/releases/latest")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    .setNegativeButton(getString(R.string.menu_magisk_ifwenhance)) { dialog, which ->
                        val uri: Uri =
                            Uri.parse("https://github.com/Kr328/Riru-IFWEnhance/releases/latest")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    .show()
                true
            }
            R.id.about_menu -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.menu_developer))
                    .setMessage(getString(R.string.menu_about_dialog))
                    .setPositiveButton(getString(R.string.menu_about_blog)) { dialog, which ->
                        val uri: Uri = Uri.parse("https://jakting.com")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
