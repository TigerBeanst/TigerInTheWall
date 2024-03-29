package com.jakting.shareclean.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


fun Context?.logd(message: String) =
    if (isDebug()) {
        Log.d("TigerBeanst", message)
    } else {
    }

fun Context?.toast(message: Any, isStringResId: Boolean = false) =
    if (isStringResId) {
        Toast.makeText(this, this!!.getString(message as Int), Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }


fun Context?.longtoast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun View.sbar(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)

fun View.sbarlong(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)

fun View.sbarin(message: CharSequence) =
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)