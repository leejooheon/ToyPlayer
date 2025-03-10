package com.jooheon.toyplayer.features.common.extension

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import com.jooheon.toyplayer.features.common.utils.VersionUtil

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
fun Context.dpToPixel(dp: Int): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val dpi = displayMetrics.densityDpi
    val density = displayMetrics.density // density에는 dip/160 값이 들어 있음.

    return (dp * density + 0.5).toInt()
}