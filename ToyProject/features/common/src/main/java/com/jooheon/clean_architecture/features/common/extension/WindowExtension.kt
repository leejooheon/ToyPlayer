package com.jooheon.clean_architecture.features.common.extension

import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.jooheon.clean_architecture.features.common.utils.VersionUtil

fun Window.setFullScreen() {
    if (VersionUtil.hasR()) {
        this.setDecorFitsSystemWindows(false)
        this.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}

fun Window.showSystemUI() {
    if (VersionUtil.hasR()) {
        this.insetsController?.apply {
            show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
    } else {
        this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}
