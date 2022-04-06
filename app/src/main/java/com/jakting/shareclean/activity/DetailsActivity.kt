package com.jakting.shareclean.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.App
import com.jakting.shareclean.databinding.ActivityDetailsBinding
import com.jakting.shareclean.utils.getAppDetail
import com.jakting.shareclean.utils.getAppIconByPackageName
import kotlinx.coroutines.launch

class DetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var app = App()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        app = intent.extras?.get("app") as App
        initView()
    }

    private fun initView() {
        binding.appName.text = app.appName
        binding.appPackageName.text = app.packageName
        binding.appVersionName.text =
            String.format(getString(R.string.manager_clean_detail_version),getAppDetail(app.packageName).versionName,getAppDetail(app.packageName).versionCode)
        lifecycleScope.launch {
            binding.appIcon.setImageDrawable(
                getAppIconByPackageName(
                    this@DetailsActivity,
                    app.packageName
                )
            )
        }
    }


}