package com.jakting.shareclean.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.BRV
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.setup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.jakting.shareclean.BR
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.App
import com.jakting.shareclean.data.AppIntent
import com.jakting.shareclean.databinding.ActivityDetailsBinding
import com.jakting.shareclean.utils.MyApplication.Companion.intentIconMap
import com.jakting.shareclean.utils.getAppDetail
import com.jakting.shareclean.utils.getAppIconByPackageName
import com.jakting.shareclean.utils.getColorFromAttr
import kotlinx.coroutines.launch


class DetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var app = App()
    private var firstShare = -1
    private var firstView = -1
    private var firstText = -1
    private var firstBrowser = -1
    private var shareSize = 0
    private var viewSize = 0
    private var textSize = 0
    private var browserSize = 0
    private var selectAllOrNone = true


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        app = intent.extras?.get("app") as App
        shareSize = intent.extras?.get("shareSize") as Int
        viewSize = intent.extras?.get("viewSize") as Int
        textSize = intent.extras?.get("textSize") as Int
        browserSize = intent.extras?.get("browserSize") as Int
        initData()
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.appName.text = app.appName
        binding.appPackageName.text = app.packageName
        binding.appVersionName.text =
            String.format(
                getString(R.string.manager_clean_detail_version),
                getAppDetail(app.packageName).versionName,
                getAppDetail(app.packageName).versionCode
            )
        lifecycleScope.launch {
            binding.appIcon.setImageDrawable(
                getAppIconByPackageName(
                    this@DetailsActivity,
                    app.packageName
                )
            )
        }
        binding.cardSystemAppWarning.visibility = if (app.isSystem) View.VISIBLE else View.GONE
        BRV.modelId = BR.app
        binding.rv.setup {
            addType<AppIntent>(R.layout.item_intent)
            onBind {
                val appIcon = findView<ImageView>(R.id.app_component_icon)
                lifecycleScope.launch {
                    val keyIcon =
                        getModel<AppIntent>().packageName + "/" + getModel<AppIntent>().component
                    appIcon.setImageDrawable(intentIconMap[keyIcon])
                }
                val appComponentScheme =
                    findView<TextView>(R.id.app_component_scheme)
                appComponentScheme.text = when (getModel<AppIntent>().type) {
                    "1_share" -> getString(R.string.manager_clean_type_send)
                    "2_share_multi" -> getString(R.string.manager_clean_type_send_multi)
                    "5_browser_http" -> getString(R.string.manager_clean_type_browser_http)
                    "6_browser_https" -> getString(R.string.manager_clean_type_browser_https)
                    else -> ""
                } + " "
                val appComponent = findView<TextView>(R.id.app_component)
                val appComponentSplit = getModel<AppIntent>().component.split(".")
                val appComponentContent = SpannableString(getModel<AppIntent>().component)
                appComponentContent.setSpan(
                    UnderlineSpan(),
                    getModel<AppIntent>().component.length - appComponentSplit.last().length,
                    getModel<AppIntent>().component.length,
                    0
                )
                appComponent.text = appComponentContent
                val cardView = findView<MaterialCardView>(R.id.app_card)
                val appComponentName = findView<TextView>(R.id.app_component_name)
                cardView.isChecked = getModel<AppIntent>().checked
                cardView.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked){
                        cardView.setCardBackgroundColor(getColorFromAttr(R.attr.colorTertiary))
                        appComponentName.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                        appComponentScheme.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                        appComponent.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                    }else{
                        cardView.setCardBackgroundColor(getColorFromAttr(R.attr.colorTertiaryContainer))
                        appComponentName.setTextColor(getColorFromAttr(R.attr.colorOnTertiaryContainer))
                        appComponentScheme.setTextColor(getColorFromAttr(R.attr.colorOnTertiaryContainer))
                        appComponent.setTextColor(getColorFromAttr(R.attr.colorOnTertiaryContainer))
                    }
                }
                cardView.setOnClickListener {
                    getModel<AppIntent>().checked = !getModel<AppIntent>().checked
                    cardView.isChecked = getModel<AppIntent>().checked
                }
                val typeLayout = findView<ConstraintLayout>(R.id.type_layout)
                val typeDetail = findView<Chip>(R.id.type_detail)
                typeDetail.apply {
                    when (modelPosition) {
                        firstShare -> {
                            typeLayout.visibility = View.VISIBLE
                            text = getString(R.string.manager_clean_type_send)
                            chipIcon = ContextCompat.getDrawable(
                                this@DetailsActivity,
                                R.drawable.ic_twotone_share_24
                            )
                        }
                        firstView -> {
                            typeLayout.visibility = View.VISIBLE
                            text = getString(R.string.manager_clean_type_view)
                            chipIcon = ContextCompat.getDrawable(
                                this@DetailsActivity,
                                R.drawable.ic_twotone_file_open_24
                            )
                        }
                        firstText -> {
                            typeLayout.visibility = View.VISIBLE
                            text = getString(R.string.manager_clean_type_text)
                            chipIcon = ContextCompat.getDrawable(
                                this@DetailsActivity,
                                R.drawable.ic_twotone_text_fields_24
                            )
                        }
                        firstBrowser -> {
                            typeLayout.visibility = View.VISIBLE
                            text = getString(R.string.manager_clean_type_browser)
                            chipIcon = ContextCompat.getDrawable(
                                this@DetailsActivity,
                                R.drawable.ic_twotone_public_24
                            )
                        }
                    }
                }
            }
        }.models = app.intentList

        binding.buttonSelect.setOnClickListener {
            for (intentIndex in app.intentList.indices) {
                app.intentList[intentIndex].checked = selectAllOrNone
                binding.rv.bindingAdapter.notifyItemChanged(intentIndex)
            }
            when (selectAllOrNone) {
                true -> { // 全选
                    binding.buttonSelect.text = getString(R.string.manager_clean_detail_select_none)
                    binding.buttonSelect.icon = ContextCompat.getDrawable(
                        this@DetailsActivity,
                        R.drawable.ic_twotone_deselect_24
                    )
                    selectAllOrNone = false
                }
                false -> { //全不选
                    binding.buttonSelect.text = getString(R.string.manager_clean_detail_select_all)
                    binding.buttonSelect.icon = ContextCompat.getDrawable(
                        this@DetailsActivity,
                        R.drawable.ic_twotone_select_all_24
                    )
                    selectAllOrNone = true
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.cleanButton) { v, insets ->
            v.updateLayoutParams {
                (this as ViewGroup.MarginLayoutParams).bottomMargin =
                    16 + insets.systemWindowInsets.bottom
            }
            insets
        }
    }

    private fun initData() {
        app.intentList.sortBy { it.type.first() }
        for (intentIndex in app.intentList.indices) {
            val tempType = app.intentList[intentIndex].type
            if (firstShare == -1 && (tempType == "1_share" || tempType == "2_share_multi")) {
                firstShare = intentIndex
            }
            if (firstView == -1 && tempType == "3_view") {
                firstView = intentIndex
            }
            if (firstText == -1 && tempType == "4_text") {
                firstText = intentIndex
            }
            if (firstBrowser == -1 && (tempType == "5_browser_http" || tempType == "6_browser_https")) {
                firstBrowser = intentIndex
            }
        }
    }


}