package com.jakting.shareclean.activity

import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.drake.brv.utils.BRV
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
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

class IntentManagerActivity : BaseActivity() {

    private lateinit var binding: ActivityCleanManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityCleanManagerBinding.inflate(layoutInflater)
        initView()
        initData()
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }

    private fun initView() {
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_layout)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 400L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_layout)
            duration = 250L
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        BRV.modelId = BR.app
//        val intent = AppIntent("send", "aaa")
//        val app = App(
//            appName = "测试dsadsadsadasffdsfsa啊啊啊啊啊啥啥啥",
//            packageName = "com.tencent.mm",
//            intentList = listOf(intent)
//        )
//        val dataaa = ArrayList<App>()
//        dataaa.add(app)
//        dataaa.add(app)
//        dataaa.add(app)
//        dataaa.add(app)
        val appList = AppInfo(
            IntentType(
                share = true,
                view = true,
                text = true,
                browser = true
            )
        ).getAppList()
        binding.managerCleanRecyclerView.linear().setup {
            addType<App>(R.layout.item_manager_clean)
            onBind {
                val appIcon = findView<ImageView>(R.id.app_icon)
                appIcon.setImageDrawable(getAppIconByPackageName(getModel<App>().packageName))
            }
        }.models = appList
    }
}