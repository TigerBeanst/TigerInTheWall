package com.jakting.shareclean.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.net.Get
import com.drake.net.NetConfig
import com.drake.net.okhttp.setConverter
import com.drake.net.utils.scopeNetLife
import com.drake.statelayout.StateLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.data.AppIntent
import com.jakting.shareclean.data.QuickCleanListApiResult
import com.jakting.shareclean.data.QuickCleanRuleEntityListApiResult
import com.jakting.shareclean.databinding.ActivityQuickCleanBinding
import com.jakting.shareclean.utils.*
import com.jakting.shareclean.utils.application.Companion.appContext
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


private var tempRuleId = ""
private var tempRuleName = ""
private var tempRuleDesc = ""
private var tempRuleUpdate: Long = 0
private var tempRuleUpload: Long = 0
private var firstShare = -1
private var firstView = -1
private var firstText = -1
private var firstBrowser = -1
lateinit var thisActivity: Context

class QuickCleanActivity : BaseActivity() {

    private lateinit var binding: ActivityQuickCleanBinding
    private lateinit var searchView: SearchView
    private lateinit var searchListener: SearchView.OnQueryTextListener
    lateinit var dataList: MutableList<QuickCleanListApiResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityQuickCleanBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        thisActivity = this@QuickCleanActivity
        NetConfig.initialize(getBaseApi()) {
            setConverter(SerializationConverter())
        }
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manager_intent_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
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

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        searchListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.managerQuickCleanRecyclerView.models =
                    dataList.filter {
                        it.ruleInfo.defaultLang.ruleName.contains(query)
                                || it.ruleInfo.defaultLang.ruleDesc.contains(query)
                                || it.ruleInfo.i18n.any { itt ->
                            itt.ruleName.contains(query)
                                    || itt.ruleDesc.contains(query)
                        }
                    }
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                binding.managerQuickCleanRecyclerView.models =
                    dataList.filter {
                        it.ruleInfo.defaultLang.ruleName.contains(query)
                                || it.ruleInfo.defaultLang.ruleDesc.contains(query)
                                || it.ruleInfo.i18n.any { itt ->
                            itt.ruleName.contains(query)
                                    || itt.ruleDesc.contains(query)
                        }
                    }
                return false
            }
        }

        binding.managerQuickCleanRecyclerView.linear().setup {
            addType<QuickCleanListApiResult>(R.layout.item_list_quick_clean_rules)
            onBind {
                val model = getModel<QuickCleanListApiResult>()
                findView<TextView>(R.id.rule_name).text = model.ruleInfo.defaultLang.ruleName
                findView<TextView>(R.id.rule_desc).text = model.ruleInfo.defaultLang.ruleDesc
                findView<TextView>(R.id.rule_update_time).text =
                    String.format(
                        getString(R.string.manager_quick_clean_update_at),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                            Instant.ofEpochSecond(model.updateTime).atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                        )
                    )
                if (model.ruleInfo.i18n.any {
                        it.ruleLang == Locale.getDefault().toLanguageTag()
                    }) {
                    val i18nRuleInfo = model.ruleInfo.i18n.first {
                        it.ruleLang == Locale.getDefault().toLanguageTag()
                    }
                    findView<TextView>(R.id.rule_name).text = i18nRuleInfo.ruleName
                    findView<TextView>(R.id.rule_desc).text = i18nRuleInfo.ruleDesc
                }

            }
            onClick(R.id.app_layout) {

                val model = getModel<QuickCleanListApiResult>()
                tempRuleId = model.ruleId
                tempRuleName = model.ruleInfo.defaultLang.ruleName
                tempRuleDesc = model.ruleInfo.defaultLang.ruleDesc
                tempRuleUpdate = model.updateTime
                tempRuleUpload = model.uploadTime
                if (model.ruleInfo.i18n.any {
                        it.ruleLang == Locale.getDefault().toLanguageTag()
                    }) {
                    val i18nRuleInfo = model.ruleInfo.i18n.first {
                        it.ruleLang == Locale.getDefault().toLanguageTag()
                    }
                    tempRuleName = i18nRuleInfo.ruleName
                    tempRuleDesc = i18nRuleInfo.ruleDesc
                }
                val quickCleanBottomSheet = QuickCleanBottomSheet()
                quickCleanBottomSheet.show(supportFragmentManager, QuickCleanBottomSheet.TAG)
            }
        }

        binding.managerQuickCleanStateLayout.onRefresh {
            scopeNetLife {
                // 这里后端直接返回的Json数组
                dataList = Get<MutableList<QuickCleanListApiResult>>("/rules-list.json").await()
                binding.managerQuickCleanRecyclerView.models = dataList
                binding.managerQuickCleanStateLayout.showContent()
            }
        }.showLoading()

    }

    class QuickCleanBottomSheet : BottomSheetDialogFragment() {

        @SuppressLint("SetTextI18n")
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.sheet_quick_clean, container, false)
            view.findViewById<TextView>(R.id.sheet_rule_name).text = tempRuleName
            view.findViewById<TextView>(R.id.sheet_rule_desc).text = tempRuleDesc
            view.findViewById<TextView>(R.id.sheet_rule_update_time).text = String.format(
                getString(R.string.manager_quick_clean_update_at),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                    Instant.ofEpochSecond(tempRuleUpdate).atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                )
            )
            view.findViewById<TextView>(R.id.sheet_rule_upload_time).text = String.format(
                getString(R.string.manager_quick_clean_upload_at),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                    Instant.ofEpochSecond(tempRuleUpload).atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                )
            )

            val sl = view.findViewById<StateLayout>(R.id.sheet_state_layout)
            val rv = view.findViewById<RecyclerView>(R.id.sheet_recycler_view)
            rv.linear().setup {
                addType<AppIntent>(R.layout.item_quick_clean_rule)
                onBind {
                    val model = getModel<AppIntent>()

                    //加载应用图标
                    val appIcon = findView<ImageView>(R.id.quick_clean_component_icon)
                    lifecycleScope.launch {
                        appIcon.setImageDrawable(model.icon)
                    }
                    //显示应用分类
                    val appComponentScheme =
                        findView<TextView>(R.id.quick_clean_component_scheme)
                    appComponentScheme.text = when (model.type) {
                        "1_share" -> getString(R.string.manager_clean_type_send)
                        "2_share_multi" -> getString(R.string.manager_clean_type_send_multi)
                        "3_view" -> getString(R.string.manager_clean_type_view)
                        "4_text" -> getString(R.string.manager_clean_type_text)
                        "5_browser" -> getString(R.string.manager_clean_type_browser_http)
                        else -> ""
                    } + " "
                    val appComponent = findView<TextView>(R.id.quick_clean_component)
                    val appComponentSplit = model.component.split(".")
                    val appComponentContent = SpannableString(model.component)
                    appComponentContent.setSpan(
                        UnderlineSpan(),
                        model.component.length - appComponentSplit.last().length,
                        model.component.length,
                        0
                    )
                    appComponent.text = appComponentContent
                    //选中时状态变更
                    val cardView = findView<MaterialCardView>(R.id.quick_clean_card)
                    val appComponentName = findView<TextView>(R.id.quick_clean_component_name)
                    appComponentName.text = model.componentName
                    cardView.isChecked = getModel<AppIntent>().checked
                    if (getModel<AppIntent>().checked) {
                        cardView.setCardBackgroundColor(thisActivity.getColorFromAttr(R.attr.colorTertiary))
                        appComponentName.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiary))
                        appComponentScheme.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiary))
                        appComponent.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiary))
                    }
                    cardView.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            cardView.setCardBackgroundColor(thisActivity.getColorFromAttr(R.attr.colorTertiary))
                            appComponentName.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiary))
                            appComponentScheme.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiary))
                            appComponent.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiary))
                        } else {
                            cardView.setCardBackgroundColor(thisActivity.getColorFromAttr(R.attr.colorTertiaryContainer))
                            appComponentName.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiaryContainer))
                            appComponentScheme.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiaryContainer))
                            appComponent.setTextColor(thisActivity.getColorFromAttr(R.attr.colorOnTertiaryContainer))
                        }
                    }
                    cardView.setOnClickListener {
                        getModel<AppIntent>().checked = !getModel<AppIntent>().checked
                        cardView.isChecked = getModel<AppIntent>().checked
                    }
                    val typeLayout = findView<ConstraintLayout>(R.id.quick_clean_type_layout)
                    val typeDetail = findView<Chip>(R.id.quick_clean_type_detail)
                    typeDetail.apply {
                        when (modelPosition) {
                            //第一个 X 分类的应用，显示类型
                            firstShare -> {
                                typeLayout.visibility = View.VISIBLE
                                text = getString(R.string.manager_clean_type_send)
                                chipIcon = ContextCompat.getDrawable(
                                    appContext,
                                    R.drawable.ic_twotone_share_24
                                )
                            }
                            firstView -> {
                                typeLayout.visibility = View.VISIBLE
                                text = getString(R.string.manager_clean_type_view)
                                chipIcon = ContextCompat.getDrawable(
                                    appContext,
                                    R.drawable.ic_twotone_file_open_24
                                )
                            }
                            firstText -> {
                                typeLayout.visibility = View.VISIBLE
                                text = getString(R.string.manager_clean_type_text)
                                chipIcon = ContextCompat.getDrawable(
                                    appContext,
                                    R.drawable.ic_twotone_text_fields_24
                                )
                            }
                            firstBrowser -> {
                                typeLayout.visibility = View.VISIBLE
                                text = getString(R.string.manager_clean_type_browser)
                                chipIcon = ContextCompat.getDrawable(
                                    appContext,
                                    R.drawable.ic_twotone_public_24
                                )
                            }
                        }
                    }
                }
            }


            val ruleList = mutableListOf<AppIntent>()
            sl.onRefresh {
                scopeNetLife {
                    // 这里后端直接返回的Json数组
                    val tempRuleList =
                        Get<QuickCleanRuleEntityListApiResult>("/rules/${tempRuleId}.json").await()
                    tempRuleList.rules.forEach {
                        val ruleSplit = it.split("/")
                        val realType = ruleSplit[0]
                        val realPackageName = ruleSplit[1]
                        val realComponent = ruleSplit[2]
                        if (realPackageName.isInstall()) {
                            val componentName = ComponentName(realPackageName, realComponent)
                            val resolveInfoList = appContext.packageManager!!.queryIntentActivities(
                                Intent(getIFWAction(realType)).setComponent(componentName)
                                    .setType("*/*"),
                                PackageManager.MATCH_ALL
                            )
                            if (resolveInfoList.size == 1) {
                                val appIntent = AppIntent(
                                    realPackageName,
                                    realComponent,
                                    (resolveInfoList[0].loadLabel(appContext.packageManager!!) as String)
                                        .replace("\n", ""),
                                    true,
                                    realType,
                                    resolveInfoList[0].loadIcon(appContext.packageManager!!)
                                )
                                ruleList.add(appIntent)
                            }
                        }
                    }
                    ruleList.sortData()
                    rv.models = ruleList
                    sl.showContent()
                }
            }.showLoading()

            return view
        }

        companion object {
            const val TAG = "QuickCleanBottomSheet"
        }
    }
}

private fun MutableList<AppIntent>.sortData() {
    this.sortBy { it.type.first() }
    for (intentIndex in this.indices) {
        val tempType = this[intentIndex].type
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
}