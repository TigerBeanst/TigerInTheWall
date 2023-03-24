package com.jakting.shareclean.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.setup
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.App
import com.jakting.shareclean.data.AppIntent
import com.jakting.shareclean.databinding.ActivityDetailsBinding
import com.jakting.shareclean.utils.application.Companion.kv
import com.jakting.shareclean.utils.deleteIfwFiles
import com.jakting.shareclean.utils.getAppDetail
import com.jakting.shareclean.utils.getAppIcon
import com.jakting.shareclean.utils.getColorFromAttr
import com.jakting.shareclean.utils.logd
import com.jakting.shareclean.utils.toast
import com.jakting.shareclean.utils.writeIfwFiles
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
    private var textTSize = 0
    private var browserSize = 0
    private var selectAllOrNone = true
    private val selectAllOrNoneType: MutableList<Boolean> = mutableListOf(true, true, true, true)


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        app = intent.extras?.get("app") as App
        shareSize = intent.extras?.get("shareSize") as Int
        viewSize = intent.extras?.get("viewSize") as Int
        textTSize = intent.extras?.get("textSize") as Int
        browserSize = intent.extras?.get("browserSize") as Int
        initData()
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.appName.text = app.appName
        binding.appPackageName.text = app.packageName
        binding.appVersionName.text =
            String.format(
                getString(R.string.manager_clean_detail_version),
                getAppDetail(app.packageName).versionName,
                getAppDetail(app.packageName).versionCode
            )
        lifecycleScope.launch {
            getAppIcon(app.packageName)?.let {
                binding.appIcon.setImageDrawable(it)
            }
        }
        binding.cardSystemAppWarning.visibility = if (app.isSystem) View.VISIBLE else View.GONE

        binding.rv.setup {
            addType<AppIntent>(R.layout.item_intent)
            onBind {
                //加载应用图标
                val appIcon = findView<ImageView>(R.id.app_component_icon)
                lifecycleScope.launch {
                    getAppIcon(getModel<AppIntent>().packageName,getModel<AppIntent>().component)?.let {
                        appIcon.setImageDrawable(it)
                    }
                }
                //显示应用分类
                val appComponentScheme =
                    findView<TextView>(R.id.app_component_scheme)
                appComponentScheme.text = when (getModel<AppIntent>().type) {
                    "1_share" -> getString(R.string.manager_clean_type_send)
                    "2_share_multi" -> getString(R.string.manager_clean_type_send_multi)
                    "3_view" -> getString(R.string.manager_clean_type_view)
                    "4_text" -> getString(R.string.manager_clean_type_text)
                    "5_browser" -> getString(R.string.manager_clean_type_browser_http)
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
                //选中时状态变更
                val cardView = findView<MaterialCardView>(R.id.app_card)
                val appComponentName = findView<TextView>(R.id.app_component_name)
                appComponentName.text = getModel<AppIntent>().componentName
                cardView.isChecked = getModel<AppIntent>().checked
                if (getModel<AppIntent>().checked) {
                    cardView.setCardBackgroundColor(getColorFromAttr(R.attr.colorTertiary))
                    appComponentName.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                    appComponentScheme.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                    appComponent.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                }
                cardView.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cardView.setCardBackgroundColor(getColorFromAttr(R.attr.colorTertiary))
                        appComponentName.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                        appComponentScheme.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                        appComponent.setTextColor(getColorFromAttr(R.attr.colorOnTertiary))
                    } else {
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
                        //第一个 X 分类的应用，显示类型
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
                val typeSelectAll = findView<MaterialButton>(R.id.button_type_select)
//                val typeAdvanced = findView<MaterialButton>(R.id.button_advanced)
//                typeAdvanced.setOnClickListener { toast(getString(R.string.coming_soon)) }
                typeSelectAll.setOnClickListener { typeSelectAll.clickSelectAllType(modelPosition) }
            }
        }.models = app.intentList

        binding.buttonSelect.setOnClickListener {
            for (intentIndex in app.intentList.indices) {
                app.intentList[intentIndex].checked = selectAllOrNone
            }
            changeSelectAllOrNone()
            binding.rv.bindingAdapter.notifyDataSetChanged()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.cleanButton) { v, insets ->
            v.updateLayoutParams {
                (this as ViewGroup.MarginLayoutParams).bottomMargin =
                    16 + insets.systemWindowInsets.bottom
            }
            insets
        }

        binding.cleanButton.setOnClickListener {
            for (intentIndex in app.intentList.indices) {
                val keyName =
                    "${app.intentList[intentIndex].type}/${app.intentList[intentIndex].packageName}/${app.intentList[intentIndex].component}"
                kv.encode(keyName, app.intentList[intentIndex].checked)
                logd(keyName + " " + app.intentList[intentIndex].checked)
            }
            if (deleteIfwFiles("all") && writeIfwFiles()) toast(getString(R.string.manage_apply_success))
        }
    }

    private fun changeSelectAllOrNone() {
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

    private fun MaterialButton.setState(selectAllOrNoneTypeBoolean: Boolean) {
        if (selectAllOrNoneTypeBoolean) {
            text = getString(R.string.manager_clean_detail_select_none)
            icon = ContextCompat.getDrawable(
                this@DetailsActivity,
                R.drawable.ic_twotone_deselect_24
            )
        } else {
            text = getString(R.string.manager_clean_detail_select_all)
            icon = ContextCompat.getDrawable(
                this@DetailsActivity,
                R.drawable.ic_twotone_select_all_24
            )
        }
    }

    private fun MaterialButton.clickSelectAllType(modelPosition: Int) {
        when (modelPosition) {
            firstShare -> {
                for (i in firstShare until (firstShare + shareSize)) {
                    app.intentList[i].checked = selectAllOrNoneType[0]
                }
                this.setState(selectAllOrNoneType[0])
                selectAllOrNoneType[0] = !selectAllOrNoneType[0]

            }
            firstView -> {
                for (i in firstView until (firstView + viewSize)) {
                    app.intentList[i].checked = selectAllOrNoneType[1]
                }
                this.setState(selectAllOrNoneType[1])
                selectAllOrNoneType[1] = !selectAllOrNoneType[1]
            }
            firstText -> {
                for (i in firstText until (firstText + textTSize)) {
                    app.intentList[i].checked = selectAllOrNoneType[2]
                }
                this.setState(selectAllOrNoneType[2])
                selectAllOrNoneType[2] = !selectAllOrNoneType[2]
            }
            firstBrowser -> {
                for (i in firstBrowser until (firstBrowser + browserSize)) {
                    app.intentList[i].checked = selectAllOrNoneType[3]
                }
                this.setState(selectAllOrNoneType[3])
                selectAllOrNoneType[3] = !selectAllOrNoneType[3]
            }
        }
        binding.rv.bindingAdapter.notifyDataSetChanged()
    }


    private fun initData() {
        app.intentList.sortBy { it.type.first() }
        var countSelect = 0
        for (intentIndex in app.intentList.indices) {
            val keyName =
                app.intentList[intentIndex].type + "/" + app.intentList[intentIndex].packageName + "/" + app.intentList[intentIndex].component
            app.intentList[intentIndex].checked = kv.decodeBool(keyName)
            if (app.intentList[intentIndex].checked) {
                countSelect++
            }
            val tempType = app.intentList[intentIndex].type
            logd("初$keyName ${app.intentList[intentIndex].checked}")
            if (firstShare == -1 && (tempType == "1_share" || tempType == "2_share_multi")) {
                firstShare = intentIndex
            }
            if (firstView == -1 && tempType == "3_view") {
                firstView = intentIndex
            }
            if (firstText == -1 && tempType == "4_text") {
                firstText = intentIndex
            }
            if (firstBrowser == -1 && tempType == "5_browser") {
                firstBrowser = intentIndex
            }
        }
        if (countSelect == app.intentList.size) {
            changeSelectAllOrNone()
            selectAllOrNone = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_intent_output -> {
            var resultText = ""
            for (intentIndex in app.intentList.indices) {
                if(app.intentList[intentIndex].checked){
                    val keyName =
                        app.intentList[intentIndex].type + "/" + app.intentList[intentIndex].packageName + "/" + app.intentList[intentIndex].component
                    resultText += "\"$keyName\", "
                }
            }

            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText(null, resultText)
            clipboard.setPrimaryClip(clip)
            toast("已复制到剪贴板")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
