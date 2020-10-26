package com.jakting.shareclean.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakting.shareclean.R
import kotlinx.android.synthetic.main.activity_apps.*


open class BaseManageActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    open var recyclerView: RecyclerView? = null
    open var mSwipeLayout: SwipeRefreshLayout? = null
    open var adapterA: RecyclerView.Adapter<*>? = null
    open var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    open var isShowSystemApp = false
    open var map: MutableMap<String, Boolean>? = null
    open lateinit var sp: SharedPreferences
    open lateinit var spe: SharedPreferences.Editor
    lateinit var spSetting: SharedPreferences

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!getDarkModeStatus(this)) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }
        setContentView(R.layout.activity_apps)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(findViewById(R.id.toolbar))
        sp = this.getSharedPreferences("intent_list", Context.MODE_PRIVATE)
        spe = this.getSharedPreferences("intent_list", Context.MODE_PRIVATE).edit()
        spSetting = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mSwipeLayout = findViewById(R.id.swipe_layout)
        (mSwipeLayout as SwipeRefreshLayout).setOnRefreshListener(this)
        (mSwipeLayout as SwipeRefreshLayout).setColorSchemeResources(
            R.color.colorAccent,
            R.color.colorPrimary
        )

        recyclerViewLayoutManager = GridLayoutManager(this@BaseManageActivity, 1)
        recyclerView!!.layoutManager = recyclerViewLayoutManager
        clearIFW()
    }

    override fun onRefresh() {
        Handler().postDelayed({
            init()
            mSwipeLayout?.isRefreshing = false
        }, 1000)
    }

    open fun clearIFW() {}

    open fun init() {
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
        isShowSystemApp = spSetting.getBoolean("switch_showSystemApp", false)
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}
