package com.jakting.shareclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakting.shareclean.adapter.AppsAdapter
import com.jakting.shareclean.utils.ApkInfoExtractor
import com.jakting.shareclean.utils.SystemManager.RootCommand
import com.jakting.shareclean.utils.ifw_content
import com.jakting.shareclean.utils.logd
import com.jakting.shareclean.utils.toast
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    var recyclerView: RecyclerView? = null
    var adapterA: RecyclerView.Adapter<*>? = null
    private var mSwipeLayout: SwipeRefreshLayout? = null
    var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    lateinit var spe: SharedPreferences.Editor
    val ifw_file_path = "/data/system/ifw/RnShareClean.xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mSwipeLayout = findViewById(R.id.swipe_layout)
        (mSwipeLayout as SwipeRefreshLayout).setOnRefreshListener(this)
        (mSwipeLayout as SwipeRefreshLayout).setColorSchemeResources(
            R.color.colorAccent,
            R.color.colorPrimary
        )
        spe = this.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
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
    }

    override fun onRefresh() {
        logd("这里这里这里这里这里这里这里")
        Handler().postDelayed({
            //Thread.sleep(1000)
            init()
            mSwipeLayout?.isRefreshing = false
        }, 1000)
    }

    private fun init() {
        recyclerViewLayoutManager = GridLayoutManager(this@MainActivity, 1)

        recyclerView!!.layoutManager = recyclerViewLayoutManager

        var apkInfoExtractor = ApkInfoExtractor(this@MainActivity)
        adapterA = AppsAdapter(
            this@MainActivity,
            apkInfoExtractor.getAllInstalledApkInfo()!!
        )
        recyclerView!!.adapter = adapterA
        floating_action_button.setOnClickListener {
            var ifw: String = "<rules>\n"
            spe.clear()
            val map = (adapterA as AppsAdapter).map
            map.entries.forEach {
                //logd(it.key)
                if (it.value) {
                    val list = it.key.split('/')
                    //logd("list: $list")
                    //logd("${list[0]} // ${list[1]}")
                    spe.putBoolean("${list[0]}/${list[1]}", it.value)
                    ifw += String.format(ifw_content, list[0], list[1])
                }
            }
            ifw += "</rules>"
            spe.apply()
            //val ifw_file_path = "/sdcard/RnShareClean.xml"
            RootCommand("touch $ifw_file_path")
            RootCommand("echo '$ifw' > $ifw_file_path")
            toast("执行完成")
            //logd(ifw)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.select_all_menu -> {
                var map = (adapterA as AppsAdapter).map
                map.entries.forEach {
                    map[it.key] = true
                }
                (adapterA as AppsAdapter).notifyDataSetChanged()
                //finish()
                true
            }
            R.id.clear_menu -> {
                var map = (adapterA as AppsAdapter).map
                map.entries.forEach {
                    map[it.key] = false
                }
                (adapterA as AppsAdapter).notifyDataSetChanged()
                true
            }
            R.id.about_menu -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.menu_about))
                    .setMessage(getString(R.string.menu_about_dialog))
                    .setPositiveButton(getString(R.string.menu_about_p)) { dialog, which ->
                        val uri: Uri = Uri.parse("https://jakting.com")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    .setNegativeButton(getString(R.string.menu_about_n)) { dialog, which ->
                        val uri: Uri = Uri.parse("https://github.com/RikkaApps/Riru")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    .setNeutralButton(getString(R.string.menu_about_nn)) { dialog, which ->
                        val uri: Uri = Uri.parse("https://github.com/Kr328/Riru-IFWEnhance")
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
