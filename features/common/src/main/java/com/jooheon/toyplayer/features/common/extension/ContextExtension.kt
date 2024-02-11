package com.jooheon.toyplayer.features.common.extension

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.isTv(): Boolean {
    val uiModeManager = (getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager) ?: return false
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

inline fun <reified T: Activity> Context.findActivity(): T? {
    return if (this is ContextWrapper) {
        unwrapUntil { it is T } as? T
    } else {
        null
    }
}
inline fun Context.findAnyActivity(): Activity? {
    return findActivity<Activity>()
}

fun ContextWrapper.unwrapUntil(predicate: (Context) -> Boolean): Context? {
    var context: Context = this
    while (context is ContextWrapper) {
        if (predicate(context)) return context
        context = context.unwrap()
    }
    return if (predicate(context)) context else null
}

fun ContextWrapper.unwrap() = baseContext