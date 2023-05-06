package com.jooheon.clean_architecture.features.common.extension

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager
import android.widget.Toast
import com.jooheon.clean_architecture.features.common.utils.VersionUtil

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.isTv(): Boolean {
    val uiModeManager = (getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager) ?: return false
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}