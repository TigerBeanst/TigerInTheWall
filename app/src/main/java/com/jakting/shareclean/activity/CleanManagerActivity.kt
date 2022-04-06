package com.jakting.shareclean.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.BRV
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.jakting.shareclean.BR
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.App
import com.jakting.shareclean.data.AppInfo
import com.jakting.shareclean.databinding.ActivityCleanManagerBinding
import com.jakting.shareclean.utils.MyApplication.Companion.chipBrowser
import com.jakting.shareclean.utils.MyApplication.Companion.chipShare
import com.jakting.shareclean.utils.MyApplication.Companion.chipText
import com.jakting.shareclean.utils.MyApplication.Companion.chipView
import com.jakting.shareclean.utils.getAppIconByPackageName
import kotlinx.coroutines.launch


class CleanManagerActivity : BaseActivity() {

    private lateinit var binding: ActivityCleanManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCleanManagerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    override fun onStart() {
        super.onStart()
        setChip(chipShare, chipView, chipText, chipBrowser)
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        BRV.modelId = BR.app
        binding.managerCleanRecyclerView.linear().setup {
            addType<App>(R.layout.item_manager_clean)
            onBind {
                val appIcon = findView<ImageView>(R.id.app_icon)
                lifecycleScope.launch {
                    appIcon.setImageDrawable(
                        getAppIconByPackageName(
                            this@CleanManagerActivity,
                            getModel<App>().packageName
                        )
                    )
                }
                val shareSize =
                    getModel<App>().intentList.filter { it.type == "share" || it.type == "share_multi" }.size
                val viewSize = getModel<App>().intentList.filter { it.type == "view" }.size
                val textSize = getModel<App>().intentList.filter { it.type == "text" }.size
                val browserSize =
                    getModel<App>().intentList.filter { it.type == "browser_https" || it.type == "browser_http" }.size

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
        }

        binding.managerCleanStateLayout.onRefresh {
            lifecycleScope.launch {
                setChip(false)
                val data = AppInfo().getAppList()
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