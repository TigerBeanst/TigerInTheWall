package com.jakting.shareclean.activity

import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.drake.brv.utils.BRV
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.jakting.shareclean.BR
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.App
import com.jakting.shareclean.data.AppInfo
import com.jakting.shareclean.data.IntentType
import com.jakting.shareclean.databinding.ActivityCleanManagerBinding
import com.jakting.shareclean.utils.getAppIconByPackageName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var chipShare = true
var chipView = true
var chipText = true
var chipBrowser = true

class IntentManagerActivity : BaseActivity() {

    private lateinit var binding: ActivityCleanManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityCleanManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_layout)
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        BRV.modelId = BR.app
        var dataList = ArrayList<App>()
        binding.managerCleanRecyclerView.linear().setup {
            addType<App>(R.layout.item_manager_clean)
            onBind {
                val appIcon = findView<ImageView>(R.id.app_icon)
                appIcon.setImageDrawable(getAppIconByPackageName(getModel<App>().packageName))
            }
        }.models = dataList

        binding.managerCleanPageRefreshLayout.apply {
            setEnableRefresh(true)
            setPrimaryColorsId(R.color.colorAccent, R.color.colorPrimary)
            onRefresh {
//                setChipEnabled(false)
                GlobalScope.launch(Dispatchers.Main) {
                    dataList = initData() as ArrayList<App>
                    withContext(Dispatchers.Main) {
                        binding.managerCleanRecyclerView.models = dataList
                        binding.managerCleanPageRefreshLayout.finishRefresh()
//                        setChipEnabled(true)
                    }
                }
            }.autoRefresh()
        }

        binding.managerCleanChipShare.setOnCheckedChangeListener { _, isChecked ->
            chipShare = isChecked
            binding.managerCleanPageRefreshLayout.autoRefresh()
        }
        binding.managerCleanChipView.setOnCheckedChangeListener { _, isChecked ->
            chipView = isChecked
            binding.managerCleanPageRefreshLayout.autoRefresh()
        }
        binding.managerCleanChipText.setOnCheckedChangeListener { _, isChecked ->
            chipText = isChecked
            binding.managerCleanPageRefreshLayout.autoRefresh()
        }
        binding.managerCleanChipBrowser.setOnCheckedChangeListener { _, isChecked ->
            chipBrowser = isChecked
            binding.managerCleanPageRefreshLayout.autoRefresh()
        }


    }

    private fun initData(): List<App> {
        return AppInfo(
            IntentType(
                share = chipShare,
                view = chipView,
                text = chipText,
                browser = chipBrowser
            )
        ).getAppList()
    }

    private fun setChipEnabled(isEnabled: Boolean) {
        binding.managerCleanChipShare.isEnabled = isEnabled
        binding.managerCleanChipView.isEnabled = isEnabled
        binding.managerCleanChipText.isEnabled = isEnabled
        binding.managerCleanChipBrowser.isEnabled = isEnabled
    }
}