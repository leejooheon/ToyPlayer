package com.jooheon.toyplayer.features.commonui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.core.view.ViewCompat

@Composable
fun Int.toDp(): Dp {
    return with(LocalDensity.current) {
        this@toDp.toDp()
    }
}
@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) {
        this@toDp.toDp()
    }
}

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        this@toPx.toPx()
    }
}

@Composable
fun isSystemBarVisible(type: Int): Boolean {
    val view = LocalView.current
    val windowInsets = ViewCompat.getRootWindowInsets(view) ?: return false
    val visible = windowInsets.isVisible(type)
    return visible
}

@Composable
fun systemTopBarHeight(): Dp {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    val heightPx = if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
    val heightDp = heightPx.toDp()
//    Timber.d("systemTopBarHeight: $heightPx, $heightDp")
    return heightDp
}