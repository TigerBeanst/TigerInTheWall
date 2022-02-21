package com.jakting.shareclean.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.jakting.shareclean.BaseActivity
import com.jakting.shareclean.R
import com.jakting.shareclean.databinding.ActivityIntentManagerBinding

class IntentManagerActivity : BaseActivity() {

    private lateinit var binding: ActivityIntentManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        binding = ActivityIntentManagerBinding.inflate(layoutInflater)
        initView()
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }

    private fun initView(){
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_layout)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 500L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.activity_layout)
            duration = 250L
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}