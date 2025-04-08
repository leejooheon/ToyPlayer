package com.jooheon.toyplayer.features.player.common

import android.app.Activity
import android.content.res.Configuration
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.extension.deviceWidth
import com.jooheon.toyplayer.features.common.extension.getHeights
import com.jooheon.toyplayer.features.common.utils.VersionUtil
import com.jooheon.toyplayer.features.commonui.ext.isSystemBarVisible
import com.jooheon.toyplayer.features.commonui.ext.systemTopBarHeight
import com.jooheon.toyplayer.features.commonui.ext.toDp

fun Activity.setImmersiveFullScreen() {
    if (VersionUtil.hasR()) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }
}

@Composable
fun verticalMargin(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val verticalMargin = screenHeight * 0.03f

    return verticalMargin
}

@Composable
fun horizontalMargin(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenWidthDp.dp
    val verticalMargin = screenHeight * 0.05f

    return verticalMargin
}

//@Composable
//fun contentSize(): Int {
//    val orientation = LocalConfiguration.current.orientation
//    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 3
//}
@Composable
fun contentSize(): Int {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val insets = androidx.compose.foundation.layout.WindowInsets.safeDrawing

    val topInset = insets.getTop(density).toDp()
    val bottomInset = insets.getBottom(density).toDp()

    val screenHeight = configuration.screenHeightDp.dp
    val usableHeight = screenHeight - topInset - bottomInset

    // 카드 하나당 높이 기준 (예: 180.dp로 설정)
    val itemMinHeight = contentHeight() + cardHeight()

    // 행 개수 = usable height에서 몇 개 들어갈 수 있는지
    return maxOf(1, (usableHeight / itemMinHeight).toInt())
}

@Composable
fun contentWidth(): Dp {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val ratio = if (isLandscape) 0.15f else 0.35f
    val pixel = context.deviceWidth() * ratio

    return pixel.toDp()
}

@Composable
fun contentHeight(): Dp {
    return contentWidth() * 0.63f
}

@Composable
internal fun cardHeight(): Dp {
    return cardTopPreviewHeight() + cardBottomPreviewHeight()
}

@Composable
internal fun cardTopPreviewHeight(): Dp { // 화면 아래에 보이는거
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = UiText.StringResource(Strings.placeholder_long).asString(),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1
    )

    val textHeight = textLayoutResult.size.height.toDp()
    val titleTextPadding = (8.dp) * 2 // ContentItem.kt padding
    val imageTopPadding = 4.dp // ContentCardItem.kt padding

    val height = textHeight + titleTextPadding + imageTopPadding

    val result = (contentHeight() * 0.6f) + height
    return result
}

@Composable
internal fun cardBottomPreviewHeight(): Dp { // 맨 위에 보이는거
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = UiText.StringResource(Strings.placeholder_long).asString(),
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        constraints = Constraints.fixedWidth(1)
    )
    val textHeight = textLayoutResult.size.height.toDp()
    val cardImagePadding = 6.dp // ContentCardItem.kt padding

    val height = textHeight + cardImagePadding

    val result =  (contentHeight() * 0.4f) + height
    return result
}

@Composable
internal fun contentSpace(): Dp {
    val screenHeight = getScreenHeight()
    val size = (contentSize() + 1).toFloat()
    val contentSpace = (screenHeight - (cardHeight() * size)) / size
//    Timber.d("contentSpace[$visible]: [$contentSpace, ${cardHeight()}] ([${appUsableHeight.toDp()}, ${fullHeight.toDp()}] - ${systemTopBarHeight()} = $screenHeight)")
    return contentSpace
}

@Composable
internal fun getScreenHeight(): Dp {
    val context = LocalContext.current
    val visible = isSystemBarVisible(WindowInsetsCompat.Type.statusBars())

    val (appUsableHeight, fullHeight) = context.getHeights()
    val screenHeight = if(visible) {
        if(appUsableHeight == fullHeight) {
            fullHeight.toDp() - systemTopBarHeight()
        } else {
            appUsableHeight.toDp()
        }
    } else {
        if(appUsableHeight == fullHeight) {
            fullHeight.toDp()
        } else {
            fullHeight.toDp() - systemTopBarHeight()
        }
    }
    return screenHeight
}