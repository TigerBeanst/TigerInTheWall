package com.jakting.shareclean.adapter

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
import com.jakting.shareclean.utils.ApkInfoSend
import com.jakting.shareclean.utils.logd


class AppsAdapter : RecyclerView.Adapter<AppsAdapter.ViewHolder> {
    var context1: Context? = null
    var json = JSONArray()
    var map: MutableMap<String, Boolean> = HashMap()
    var sp: SharedPreferences? = null
    //var spe:SharedPreferences.Editor?=null

    constructor(
        context: Context,
        jsonA: JSONArray
    ) {
        context1 = context
        json = jsonA
        sp = context1?.getSharedPreferences("data", Context.MODE_PRIVATE)
        initMap()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView
        var imageView: ImageView
        var textView_App_Name: TextView
        var textView_App_Package_Name: TextView
        var check_box: CheckBox

        init {
            cardView = view.findViewById(R.id.card_view)
            imageView = view.findViewById(R.id.imageview) as ImageView
            textView_App_Name = view.findViewById(R.id.Apk_Name)
            textView_App_Package_Name = view.findViewById(R.id.Apk_Package_Name)
            check_box = view.findViewById((R.id.check_box))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view2: View =
            LayoutInflater.from(context1).inflate(R.layout.cardview_layout, parent, false)
        return ViewHolder(view2)
    }

    private fun initMap() {
        for (i in 0 until json.size) {
            val ActivityJSONObject = json.getJSONObject(i)
            map["${ActivityJSONObject["package_name"]}/${ActivityJSONObject["activity"]}"] = false
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val apkInfoExtractor = ApkInfoSend(context1)
        val ActivityJSONObject = json.getJSONObject(position)
        val PackgeName = ActivityJSONObject["package_name"] as String
        val ActivityS = ActivityJSONObject["activity"] as String
        val AppName = ActivityJSONObject["app_name"] as String
        val ActivityName = ActivityJSONObject["activity_name"] as String
        val ApplicationLabelName = "$AppName - $ActivityName"
        val drawable: Drawable =
            apkInfoExtractor.getAppIconByPackageName(PackgeName)!!
        viewHolder.textView_App_Name.text = ApplicationLabelName
        viewHolder.textView_App_Package_Name.text = ActivityS
        viewHolder.imageView.setImageDrawable(drawable)
        viewHolder.cardView.setOnClickListener {
            viewHolder.check_box.isChecked = viewHolder.check_box.isChecked != true
        }
//        var ing = 0
//        if (sp?.getBoolean("$PackgeName/$ActivityS", false) == true && ing == 0) {
//            //sp里有这个，勾选
//            viewHolder.check_box.isChecked = true
//            map["$PackgeName/$ActivityS"] = true
//            ing = 1
//        }

        viewHolder.check_box.setOnCheckedChangeListener { compoundButton, isCheckBox ->
            if (isCheckBox) {
                //saveArray
                //spe?.putBoolean("$PackgeName/$ActivityS",true)
                map["$PackgeName/$ActivityS"] = true
                logd("$PackgeName/$ActivityS 被选中")
            } else {
                //spe?.remove("$PackgeName/$ActivityS")
                map["$PackgeName/$ActivityS"] = false
                logd("$PackgeName/$ActivityS 被取消选中")
            }
        }
        viewHolder.check_box.isChecked = true && (map["$PackgeName/$ActivityS"] == true)

    }

    override fun getItemCount(): Int {
        return json.size
    }
}