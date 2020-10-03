package com.jakting.shareclean.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.jakting.shareclean.R


class AppsAdapter(context: Context, jsonA: JSONArray) :
    RecyclerView.Adapter<AppsAdapter.ViewHolder>() {
    var context1: Context? = context
    var json = jsonA
    var map: MutableMap<String, Boolean> = HashMap()
    var sp: SharedPreferences? = null
    //var spe:SharedPreferences.Editor?=null

    init {
        sp = context1?.getSharedPreferences("data", Context.MODE_PRIVATE)
        initMap()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView = view.findViewById(R.id.card_view)
        var imageView: ImageView = view.findViewById(R.id.imageview) as ImageView
        var textureAppName: TextView = view.findViewById(R.id.Apk_Name)
        var textureAppPackageName: TextView = view.findViewById(R.id.Apk_Package_Name)
        var checkBox: CheckBox = view.findViewById((R.id.check_box))

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view2: View =
            LayoutInflater.from(context1).inflate(R.layout.cardview_layout, parent, false)
        return ViewHolder(view2)
    }

    private fun initMap() {
        for (i in 0 until json.size) {
            val activityJSONObject = json.getJSONObject(i)
            map["${activityJSONObject["package_name"]}/${activityJSONObject["activity"]}"] = false
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val activityJSONObject = json.getJSONObject(position)
        val packgeName = activityJSONObject["package_name"] as String
        val activityS = activityJSONObject["activity"] as String
        val appName = activityJSONObject["app_name"] as String
        val activityName = activityJSONObject["activity_name"] as String
        val applicationLabelName = "$appName - $activityName"
        val drawable: Drawable =
            context1.getAppIconByPackageName(packgeName)!!
        viewHolder.textureAppName.text = applicationLabelName
        viewHolder.textureAppPackageName.text = activityS
        viewHolder.imageView.setImageDrawable(drawable)
        viewHolder.cardView.setOnClickListener {
            viewHolder.checkBox.isChecked = viewHolder.checkBox.isChecked != true
        }
//        var ing = 0
//        if (sp?.getBoolean("$PackgeName/$ActivityS", false) == true && ing == 0) {
//            //sp里有这个，勾选
//            viewHolder.check_box.isChecked = true
//            map["$PackgeName/$ActivityS"] = true
//            ing = 1
//        }

        viewHolder.checkBox.setOnCheckedChangeListener { compoundButton, isCheckBox ->
            if (isCheckBox) {
                //saveArray
                //spe?.putBoolean("$PackgeName/$ActivityS",true)
                map["$packgeName/$activityS"] = true
                logd("$packgeName/$activityS 被选中")
            } else {
                //spe?.remove("$PackgeName/$ActivityS")
                map["$packgeName/$activityS"] = false
                logd("$packgeName/$activityS 被取消选中")
            }
        }
        viewHolder.checkBox.isChecked = true && (map["$packgeName/$activityS"] == true)

    }

    override fun getItemCount(): Int {
        return json.size
    }
}