package com.jakting.shareclean.activity

import android.os.Bundle
import android.view.Window
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.databinding.ActivityManagerBinding

class MainTWOActivity : BaseActivity() {

    private lateinit var binding: ActivityManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        binding = ActivityManagerBinding.inflate(layoutInflater)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_two)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 500L
//            interpolator = FastOutSlowInInterpolator()
//            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_two)
            duration = 250L
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }

        setContentView(binding.root)
        super.onCreate(savedInstanceState)


//        binding.headerMain.settingButton.setOnClickListener { view ->
//            Toast.makeText(this, "wao", Toast.LENGTH_SHORT).show()
//        }
//        binding.contentMain.card1Module.cardStatus.setOnClickListener { view ->
//            Toast.makeText(this, "wao", Toast.LENGTH_SHORT).show()
//        }
//        binding.contentMain.card2Manage.cardManager.setOnClickListener { view ->
//            Toast.makeText(this, "wao", Toast.LENGTH_SHORT).show()
//        }
    }
}