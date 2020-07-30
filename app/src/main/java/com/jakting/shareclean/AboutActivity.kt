package com.jakting.shareclean

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drakeet.about.*


class AboutActivity : AbsAboutActivity() {
    override fun onCreateHeader(
        icon: ImageView,
        slogan: TextView,
        version: TextView
    ) {
        if (!getDarkModeStatus(this)) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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
        items.add(Category("介绍与帮助"))
        items.add(Card(getString(R.string.about_page_info)))
        items.add(Category("Developers"))
        items.add(
            Contributor(
                R.drawable.dev_hjthjthjt,
                "hjthjthjt",
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
                "dev_hjthjthjt",
                getString(R.string.about_page_dev_3),
                "https://github.com/Kr328"
            )
        )
        items.add(Category(getString(R.string.about_page_open_title)))
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
    }
}