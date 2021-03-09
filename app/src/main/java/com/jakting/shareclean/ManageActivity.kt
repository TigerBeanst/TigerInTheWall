package com.jakting.shareclean

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakting.shareclean.utils.*
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.activity_apps.*


open class ManageActivity : BaseActivity() {
    open var tag = ""

    var adapter: RecyclerView.Adapter<*>? = null
    var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    var isShowSystemApp = false
    var intentDataList: ArrayList<AppsAdapter.IntentData> = ArrayList()
    private lateinit var mSearchView: SearchView

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        tag = intent.getStringExtra("tag").toString()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            if (mSearchView.query.toString().isNotEmpty()) {
                val method = mSearchView.javaClass.getDeclaredMethod("onCloseClicked")
                method.isAccessible = true
                method.invoke(mSearchView)
            } else {
                onBackPressed()
            }
        }
        apps_swipeLayout.apply {
            setEnableRefresh(true)
            setPrimaryColorsId(R.color.colorAccent, R.color.colorPrimary)
            autoRefresh()
        }
        apps_swipeLayout.setOnRefreshListener {
            init(tag)
        }
        recyclerViewLayoutManager = GridLayoutManager(this@ManageActivity, 1)
        recycler_view!!.layoutManager = recyclerViewLayoutManager
        clearIFW()
        initFab()
    }


    private fun clearIFW() {
        if (clearIFW(tag)) {
            apps_swipeLayout.autoRefresh()
            recycler_view?.sbar(getString(R.string.manage_start))?.show()
        }
    }

    open fun init(tag: String) {
        if (supportActionBar != null) {
            supportActionBar!!.title = getManageTypeTitle(tag)
        }
        scrollToHideFab()
        isShowSystemApp = settingSharedPreferences.getBoolean("switch_showSystemApp", false)

        val apkInfo = ApkInfo(this, tag)
        intentDataList = apkInfo.getAllInstalledApkInfo(isShowSystemApp)
        adapter = AppsAdapter(intentDataList)
        recycler_view?.adapter = adapter
        for (intentDataIndex in intentDataList.indices) {
            if (intentListSharedPreferences.getBoolean(
                    "${intentDataList[intentDataIndex].package_name}/${intentDataList[intentDataIndex].activity}/$tag",
                    false
                )
            ) {
                intentDataList[intentDataIndex].check = true
            }
        }
        (adapter as AppsAdapter).notifyDataSetChanged()
        apps_swipeLayout.finishRefresh()
    }

    private fun scrollToHideFab() {
        //滑动隐藏 FAB
        recycler_view?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && extendedFab.visibility == View.VISIBLE) {
                    extendedFab.hide()
                } else if (dy < 0 && extendedFab.visibility != View.VISIBLE) {
                    extendedFab.show()
                }
            }
        })
    }

    private fun initFab() {
        extendedFab.setOnClickListener {
            var ifw = "<rules>\n"
            for (intentData in intentDataList) {
                intentListSharedPreferencesEditor.putBoolean(
                    "${intentData.package_name}/${intentData.activity}/$tag",
                    intentData.check
                )
                if (intentData.check) {
                    ifw += getIFWContent(tag, "${intentData.package_name}/${intentData.activity}")

                }
            }
            ifw += "</rules>"
            intentListSharedPreferencesEditor.apply()
            val filePath = getIFWPath(tag)
            if (Shell.su("touch $filePath").exec().isSuccess &&
                Shell.su("echo '$ifw' > $filePath").exec().isSuccess
            ) {
                extendedFab.setIconResource(R.drawable.ic_baseline_check_24)
                extendedFab.text = getString(R.string.manage_apply_success)
            } else {

                extendedFab.setIconResource(R.drawable.ic_round_error_24)
                extendedFab.text = getString(R.string.error)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu!!.findItem(R.id.search_menu)
        val selectAllItem = menu.findItem(R.id.select_all_menu)
        val clearSelectItem = menu.findItem(R.id.clear_select_menu)
        mSearchView = searchItem.actionView as SearchView
        val searchFrameLL = mSearchView.findViewById(R.id.search_edit_frame) as LinearLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        params.weight = 1F
        params.setMargins(dip2px(-8),0,dip2px(32),0)
        searchFrameLL.layoutParams = params
//        mSearchView.maxWidth = android.R.attr.width
        mSearchView.setOnSearchClickListener {
//            it.visibility = View.INVISIBLE
            selectAllItem.isVisible = false
            clearSelectItem.isVisible = false
        }
        mSearchView.setOnCloseListener {
            selectAllItem.isVisible = true
            clearSelectItem.isVisible = true
            false
        }
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                extendedFab.visibility = if (s.isNotEmpty()) View.INVISIBLE else View.VISIBLE
                logd("此时的搜索字符串是 $s")
                (adapter as AppsAdapter).filter.filter(s)
                (adapter as AppsAdapter).notifyDataSetChanged()
                return false
            }
        })



        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_all_menu -> {
                for (intentDataIndex in intentDataList.indices) {
                    intentDataList[intentDataIndex].check = true
                }
                (adapter as AppsAdapter).notifyDataSetChanged()
                //finish()
                true
            }
            R.id.clear_select_menu -> {
                for (intentDataIndex in intentDataList.indices) {
                    intentDataList[intentDataIndex].check = false
                }
                (adapter as AppsAdapter).notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
