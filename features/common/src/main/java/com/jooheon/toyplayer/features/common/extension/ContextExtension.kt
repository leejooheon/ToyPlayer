package com.jooheon.toyplayer.features.common.extension

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import android.widget.Toast
import com.jooheon.toyplayer.features.common.utils.VersionUtil

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.getHeights(): Pair<Int, Int> {
    val appUsableScreenSize = Point()
    val realScreenSize = Point()
    val defaultDisplay = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    defaultDisplay.getSize(appUsableScreenSize)
    defaultDisplay.getRealSize(realScreenSize)
    return appUsableScreenSize.y to realScreenSize.y
}

fun Context.deviceWidth(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (VersionUtil.hasR()) {
        windowManager.currentWindowMetrics.bounds.width()
    } else {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        size.x
    }
}
fun Context.deviceHeight(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return if (VersionUtil.hasR()) {
        windowManager.currentWindowMetrics.bounds.height()
    } else {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        size.y
    }
}