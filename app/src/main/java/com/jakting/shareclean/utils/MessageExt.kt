package com.jakting.shareclean.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

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
    context: String,
    otherTitle: String = "",
    onOther: (Any, Any) -> Unit,
    cancelTitle: String = "",
    onCancel: (Any, Any) -> Unit,
    rightTitle: String = "",
    onRight: (Any, Any) -> Unit
) {
    val dialogBuilder = MaterialAlertDialogBuilder(this)
    if (title.isNotEmpty())
        dialogBuilder.setTitle(title)
    dialogBuilder.setMessage(context)
    if (otherTitle.isNotEmpty()) {
        dialogBuilder.setNeutralButton(otherTitle) { dialog, which ->
            onOther(dialog, which)
        }
    }
    if (otherTitle.isNotEmpty()) {
        dialogBuilder.setNeutralButton(otherTitle) { dialog, which ->
            onOther(dialog, which)
        }
    }
    if (cancelTitle.isNotEmpty()) {
        dialogBuilder.setNegativeButton(cancelTitle) { dialog, which ->
            onCancel(dialog, which)
        }
    }
    if (rightTitle.isNotEmpty()) {
        dialogBuilder.setPositiveButton(rightTitle) { dialog, which ->
            onRight(dialog, which)
        }
    }
    dialogBuilder.show()
}