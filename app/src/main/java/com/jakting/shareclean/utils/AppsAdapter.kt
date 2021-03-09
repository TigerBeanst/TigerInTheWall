package com.jakting.shareclean.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.jakting.shareclean.R


class AppsAdapter(val intentDataListOrigin: ArrayList<IntentData>) :
    RecyclerView.Adapter<AppsAdapter.ViewHolder>(), Filterable {
    lateinit var parentContext: Context
    var intentDataList = intentDataListOrigin

    data class IntentData(
        var app_name: String,
        var package_name: String,
        var activity: String,
        var activity_name: String,
        var check: Boolean
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appLayout: LinearLayout = view.findViewById(R.id.item_intent_app_layout)
        var appIcon: ImageView = view.findViewById(R.id.item_intent_app_icon) as ImageView
        var appLabelName: TextView = view.findViewById(R.id.item_intent_app_label_name)
        var appActivitiesName: TextView = view.findViewById(R.id.item_intent_app_activities_name)
        var appCheckbox: CheckBox = view.findViewById((R.id.item_intent_app_checkbox))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        parentContext = parent.context
        val view: View =
            LayoutInflater.from(parentContext).inflate(R.layout.item_intent, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val intentData = intentDataList[position]
        val appName = intentData.app_name
        val packageName = intentData.package_name
        val activity = intentData.activity
        val activityName = intentData.activity_name
        val appLabelName = "$appName - $activityName"
        val appIconDrawable: Drawable =
            parentContext.getAppIconByPackageName(packageName)!!
        viewHolder.appLabelName.text = appLabelName
        viewHolder.appActivitiesName.text = activity
        viewHolder.appIcon.setImageDrawable(appIconDrawable)
        viewHolder.appLayout.setOnClickListener {
            viewHolder.appCheckbox.isChecked = !viewHolder.appCheckbox.isChecked
            intentDataList[position].check = viewHolder.appCheckbox.isChecked
        }
        viewHolder.appCheckbox.setOnCheckedChangeListener { _, isCheck ->
            intentDataList[position].check = isCheck
        }
        viewHolder.appCheckbox.isChecked = true && (intentDataList[position].check)


    }

    override fun getItemCount(): Int {
        return intentDataList.size
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterResults = FilterResults()
                val intentDataListFiltered: ArrayList<IntentData> = ArrayList()
                val charString = charSequence.toString()
                logd("charString 为 【$charString】，长度为【${charString.length}】")
                if (charString.isEmpty()) {
                    logd("现在输入为空")
                    filterResults.count = intentDataListOrigin.size
                    filterResults.values = intentDataListOrigin
                } else {
                    logd("现在输入为 $charString")
                    for (intentData in intentDataListOrigin) {
                        //这里根据需求，添加匹配规则
                        if (intentData.app_name.contains(charString,true) ||
                            intentData.package_name.contains(charString,true) ||
                            intentData.activity.contains(charString,true) ||
                            intentData.activity_name.contains(charString,true)
                        ) {
                            intentDataListFiltered.add(intentData)
                        }
                    }
                    filterResults.count = intentDataListFiltered.size
                    filterResults.values = intentDataListFiltered
                }
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                intentDataList = filterResults.values as ArrayList<IntentData>
                //刷新数据
                notifyDataSetChanged()
            }
        }
    }
}