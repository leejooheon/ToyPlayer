package com.jooheon.clean_architecture.presentation.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color

import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import com.jooheon.clean_architecture.presentation.R
import kotlinx.coroutines.*

fun showToastMessage(context: Context, message: String?) {
    Toast.makeText(context, message ?: context.resources.getString(R.string.some_error), Toast.LENGTH_SHORT)
        .show()
}

fun showLoadingDialog(activity: Activity?): Dialog? {
    if (activity == null || activity.isFinishing) {
        return null
    }
    val progressDialog = Dialog(activity, R.style.ProgressDialogTheme)

    if (progressDialog.window != null) {
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    progressDialog.show()

    progressDialog.setContentView(R.layout.progress_dialog)
    progressDialog.setCancelable(false)
    progressDialog.setCanceledOnTouchOutside(false)

    return progressDialog
}

fun hideLoadingDialog(dialog: Dialog?, activity: Activity?, runningTime: Long) {
    if (activity != null && !activity.isFinishing && dialog != null && dialog.isShowing) {
        val leastTime = 1000
        if(runningTime > leastTime) {
            dialog.dismiss()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                delay(leastTime - runningTime)
                withContext(Dispatchers.Main) {
                    dialog?.dismiss()
                }
            }
        }
    }
}

fun forcehideLoadingDialog(dialog: Dialog?, activity: Activity?) {
    if (activity != null && !activity.isFinishing && dialog != null && dialog.isShowing) {
        dialog.dismiss()
    }
}