package com.jakting.shareclean

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.widget.ImageView
import android.widget.TextView
import com.drakeet.about.*


class AboutActivity : AbsAboutActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreateHeader(
        icon: ImageView,
        slogan: TextView,
        version: TextView
    ) {
        icon.setImageResource(R.mipmap.ic_launcher)
        slogan.text = getString(R.string.app_name)
        version.text = "v" + BuildConfig.VERSION_NAME
    }

    private fun getDarkModeStatus(context: Context): Boolean {
        val mode: Int =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.add(Category(getString(R.string.about_page_info)))
        items.add(Card(getString(R.string.about_page_info_desc)))
        items.add(Category(getString(R.string.about_page_dev)))
        items.add(
            Contributor(
                R.drawable.dev_tigerbeanst,
                "TigerBeanst",
                getString(R.string.about_page_dev_1),
                "https://jakting.com"
            )
        )
        items.add(
            Contributor(
                R.drawable.dev_rikkaw,
                "RikkaW",
                getString(R.string.about_page_dev_2),
                "https://github.com/RikkaW"
            )
        )
        items.add(
            Contributor(
                R.drawable.dev_kr328,
                "Kr328",
                getString(R.string.about_page_dev_3),
                "https://github.com/Kr328"
            )
        )
        items.add(Category(getString(R.string.about_page_open_source)))
        items.add(
            License(
                "MultiType",
                "drakeet",
                License.APACHE_2,
                "https://github.com/drakeet/MultiType"
            )
        )
        items.add(
            License(
                "about-page",
                "drakeet",
                License.APACHE_2,
                "https://github.com/drakeet/about-page"
            )
        )
        items.add(
            License(
                "fastjson",
                "Alibaba",
                License.APACHE_2,
                "https://github.com/alibaba/fastjson"
            )
        )
        items.add(
            License(
                "libsu",
                "topjohnwu",
                License.APACHE_2,
                "https://github.com/topjohnwu/libsu"
            )
        )
        items.add(
            License(
                "SmartRefreshLayout",
                "scwang90",
                License.APACHE_2,
                "https://github.com/scwang90/SmartRefreshLayout"
            )
        )
        items.add(
            License(
                "Localization",
                "akexorcist",
                License.APACHE_2,
                "https://github.com/akexorcist/Localization"
            )
        )
        items.add(
            License(
                "Kotlin stdlib",
                "JetBrains",
                License.APACHE_2,
                "https://github.com/JetBrains/kotlin/"
            )
        )
        items.add(
            License(
                "AndroidX Core",
                "Google",
                License.APACHE_2,
                "https://source.android.com/"
            )
        )
        items.add(
            License(
                "AndroidX ConstraintLayout",
                "Google",
                License.APACHE_2,
                "https://source.android.com/"
            )
        )
        items.add(
            License(
                "AndroidX RecyclerView",
                "Google",
                License.APACHE_2,
                "https://source.android.com/"
            )
        )
        items.add(
            License(
                "AndroidX CardView",
                "Google",
                License.APACHE_2,
                "https://source.android.com/"
            )
        )
        items.add(
            License(
                "AndroidX Preference",
                "Google",
                License.APACHE_2,
                "https://source.android.com/"
            )
        )
        items.add(
            License(
                "Material Components",
                "Google",
                License.APACHE_2,
                "https://github.com/material-components/material-components-android"
            )
        )
    }
}
