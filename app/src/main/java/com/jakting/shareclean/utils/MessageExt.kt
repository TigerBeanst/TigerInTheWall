package com.jakting.shareclean.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.jakting.shareclean.utils.application.Companion.appContext
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment


fun logd(message: String) =
    Log.d("TigerBeanst", message)

fun Context?.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context?.longtoast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun View.sbar(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)

fun View.sbarlong(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)

fun View.sbarin(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)

fun Context.mdDialog(
    title: String,
    content: String,
    animation: String,
    cancelAble: Boolean = true
): BottomSheetMaterialDialog.Builder {
    return BottomSheetMaterialDialog.Builder(this as Activity)
        .setTitle(title, TextAlignment.START)
        .setMessage(content, TextAlignment.START)
        .setCancelable(cancelAble)
        .setAnimation(animation + if (isDarkMode()) "_dark" else "_light" + ".json") as BottomSheetMaterialDialog.Builder
}

fun (BottomSheetMaterialDialog.Builder).show(lottiePx: Int) {
    val mDialog = this.build()
    val animationView: LottieAnimationView = mDialog.animationView
    val layoutParams = animationView.layoutParams
    layoutParams.height = appContext.getPxFromDp(lottiePx)
    animationView.layoutParams = layoutParams
    mDialog.show()
}