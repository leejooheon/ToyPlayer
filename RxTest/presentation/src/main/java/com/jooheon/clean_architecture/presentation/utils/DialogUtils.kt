package com.jooheon.clean_architecture.presentation.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color

import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import com.jooheon.clean_architecture.presentation.R

fun showToastMessage(context: Context, message: String?) {
    Toast.makeText(context, message ?: context.resources.getString(R.string.some_error), Toast.LENGTH_SHORT)
        .show()
}

fun showLoadingDialog(activity: Activity?): Dialog? {
    if (activity == null || activity.isFinishing) {
        return null
    }
    val progressDialog = Dialog(activity, R.style.ProgressDialogTheme)

    progressDialog.show()
    if (progressDialog.window != null) {
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    progressDialog.setContentView(R.layout.progress_dialog)
    progressDialog.setCancelable(false)
    progressDialog.setCanceledOnTouchOutside(false)
//    dialog.isIndeterminate = true

    progressDialog.show()

    return progressDialog
}

fun hideLoadingDialog(dialog: Dialog?, activity: Activity?) {
    if (activity != null && !activity.isFinishing && dialog != null && dialog.isShowing) {
        dialog.dismiss()
    }
}