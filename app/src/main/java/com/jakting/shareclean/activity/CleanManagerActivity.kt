package com.jakting.shareclean.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.App
import com.jakting.shareclean.data.AppInfo
import com.jakting.shareclean.databinding.ActivityCleanManagerBinding
import com.jakting.shareclean.utils.application.Companion.chipBrowser
import com.jakting.shareclean.utils.application.Companion.chipShare
import com.jakting.shareclean.utils.application.Companion.chipText
import com.jakting.shareclean.utils.application.Companion.chipView
import com.jakting.shareclean.utils.application.Companion.settingSharedPreferences
import com.jakting.shareclean.utils.getAppIcon
import kotlinx.coroutines.launch
import java.util.Locale


class CleanManagerActivity : BaseActivity() {

    private lateinit var binding: ActivityCleanManagerBinding
    private var applicationIconMap = hashMapOf<String, Drawable>()
    private lateinit var searchListener: SearchView.OnQueryTextListener
    lateinit var data: List<App>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCleanManagerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_manager_clean, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val searchView = menu.findItem(R.id.menu_manager_clean_search).actionView as SearchView
        searchView.setOnQueryTextListener(searchListener)
        searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(arg0: View) {
                binding.appBarLayout.setExpanded(false, true)
            }

            override fun onViewDetachedFromWindow(v: View) {
                binding.appBarLayout.setExpanded(false, true)
            }
        })
        searchView.findViewById<View>(androidx.appcompat.R.id.search_edit_frame).layoutDirection =
            View.LAYOUT_DIRECTION_INHERIT
        searchView.findViewById<View>(androidx.appcompat.R.id.search_plate).background = null
        searchView.findViewById<View>(androidx.appcompat.R.id.search_mag_icon).visibility =
            View.GONE
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        setChip(chipShare, chipView, chipText, chipBrowser)
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        searchListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                binding.managerCleanRecyclerView.models =
                    data.filter {
                        it.appName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
                                || it.packageName.lowercase(Locale.ROOT)
                            .contains(query.lowercase(Locale.ROOT))
                    }
                return false
            }
        }

        binding.managerCleanRecyclerView.linear().setup {
            addType<App>(R.layout.item_manager_clean)
            onBind {
                findView<TextView>(R.id.app_name).text = getModel<App>().appName
                findView<TextView>(R.id.app_package_name).text = getModel<App>().packageName

                findView<ImageView>(R.id.app_icon_share).visibility =
                    if (getModel<App>().hasType.share) View.VISIBLE else View.GONE
                findView<TextView>(R.id.app_intent_count_share).visibility =
                    if (getModel<App>().hasType.share) View.VISIBLE else View.GONE
                findView<ImageView>(R.id.app_icon_view).visibility =
                    if (getModel<App>().hasType.view) View.VISIBLE else View.GONE
                findView<TextView>(R.id.app_intent_count_view).visibility =
                    if (getModel<App>().hasType.view) View.VISIBLE else View.GONE
                findView<ImageView>(R.id.app_icon_text).visibility =
                    if (getModel<App>().hasType.text) View.VISIBLE else View.GONE
                findView<TextView>(R.id.app_intent_count_text).visibility =
                    if (getModel<App>().hasType.text) View.VISIBLE else View.GONE
                findView<ImageView>(R.id.app_icon_browser).visibility =
                    if (getModel<App>().hasType.browser) View.VISIBLE else View.GONE
                findView<TextView>(R.id.app_intent_count_browser).visibility =
                    if (getModel<App>().hasType.browser) View.VISIBLE else View.GONE

                val appIcon = findView<ImageView>(R.id.app_icon)
                lifecycleScope.launch {
                    if (applicationIconMap.containsKey(getModel<App>().packageName)) {
                        appIcon.setImageDrawable(applicationIconMap[getModel<App>().packageName])
                    } else {
                        getAppIcon(getModel<App>().packageName)?.let {
                            appIcon.setImageDrawable(it)
                            applicationIconMap[getModel<App>().packageName] = it
                        }

                    }
                }
                val shareSize =
                    getModel<App>().intentList.filter { it.type == "1_share" || it.type == "2_share_multi" }.size
                val viewSize = getModel<App>().intentList.filter { it.type == "3_view" }.size
                val textSize = getModel<App>().intentList.filter { it.type == "4_text" }.size
                val browserSize = getModel<App>().intentList.filter { it.type == "5_browser" }.size

                if (!((shareSize > 0 && chipShare) ||
                            (viewSize > 0 && chipView) ||
                            (textSize > 0 && chipText) ||
                            (browserSize > 0 && chipBrowser))
                ) {
                    itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }

                findView<ImageView>(R.id.app_icon_system).visibility =
                    when (getModel<App>().isSystem) {
                        true -> View.VISIBLE
                        else -> View.GONE
                    }
                findView<TextView>(R.id.app_intent_count_share).text = shareSize.toString()
                findView<TextView>(R.id.app_intent_count_view).text = viewSize.toString()
                findView<TextView>(R.id.app_intent_count_text).text = textSize.toString()
                findView<TextView>(R.id.app_intent_count_browser).text = browserSize.toString()
            }
            onClick(R.id.app_layout) {
                val intent = Intent(this@CleanManagerActivity, DetailsActivity::class.java)
                intent.putExtra("app", getModel<App>())
                intent.putExtra(
                    "shareSize",
                    itemView.findViewById<TextView>(R.id.app_intent_count_share).text.toString()
                        .toInt()
                )
                intent.putExtra(
                    "viewSize",
                    itemView.findViewById<TextView>(R.id.app_intent_count_view).text.toString()
                        .toInt()
                )
                intent.putExtra(
                    "textSize",
                    itemView.findViewById<TextView>(R.id.app_intent_count_text).text.toString()
                        .toInt()
                )
                intent.putExtra(
                    "browserSize",
                    itemView.findViewById<TextView>(R.id.app_intent_count_browser).text.toString()
                        .toInt()
                )
                startActivity(intent)
            }
        }

        binding.managerCleanStateLayout.onRefresh {
            lifecycleScope.launch {
                setChip(false)
                data = if (settingSharedPreferences.getBoolean("pref_system_app", true)) {
                    AppInfo().getAppList()
                        .filter { !it.packageName.startsWith("com.jakting.shareclean") }
                } else {
                    AppInfo().getAppList()
                        .filter { !it.isSystem && !it.packageName.startsWith("com.jakting.shareclean") }
                }
                binding.managerCleanRecyclerView.models = data
                setChip(true)
                binding.managerCleanStateLayout.showContent()
            }
        }.showLoading()


        binding.managerCleanChipShare.setOnCheckedChangeListener { _, isChecked ->
            chipShare = isChecked
            binding.managerCleanStateLayout.showLoading()
        }
        binding.managerCleanChipView.setOnCheckedChangeListener { _, isChecked ->
            chipView = isChecked
            binding.managerCleanStateLayout.showLoading()
        }
        binding.managerCleanChipText.setOnCheckedChangeListener { _, isChecked ->
            chipText = isChecked
            binding.managerCleanStateLayout.showLoading()
        }
        binding.managerCleanChipBrowser.setOnCheckedChangeListener { _, isChecked ->
            chipBrowser = isChecked
            binding.managerCleanStateLayout.showLoading()
        }


    }

    private fun setChip(vararg args: Boolean) {
        if (args.size == 1) { // 禁用/启用
            binding.managerCleanChipShare.isEnabled = args[0]
            binding.managerCleanChipView.isEnabled = args[0]
            binding.managerCleanChipText.isEnabled = args[0]
            binding.managerCleanChipBrowser.isEnabled = args[0]
        } else { // 设置图标状态
            binding.managerCleanChipShare.isChecked = args[0]
            binding.managerCleanChipView.isChecked = args[1]
            binding.managerCleanChipText.isChecked = args[2]
            binding.managerCleanChipBrowser.isChecked = args[3]
        }
    }
}
