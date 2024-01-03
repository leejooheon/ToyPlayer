package com.jooheon.toyplayer.features.common.extension

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.isTv(): Boolean {
    val uiModeManager = (getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager) ?: return false
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}