package com.jooheon.toyplayer.features.player.common

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import com.jooheon.toyplayer.features.common.extension.deviceWidth
import com.jooheon.toyplayer.features.commonui.ext.toDp
import com.jooheon.toyplayer.features.player.component.info.content.cardBottomPreviewHeight
import com.jooheon.toyplayer.features.player.component.info.content.cardTopPreviewHeight

@Composable
fun horizontalMargin(): Dp {
    val screenHeight = LocalWindowInfo.current.containerSize.height.toDp()
    val verticalMargin = screenHeight * 0.05f
    return verticalMargin
}

@Composable
fun contentSize(): Int {
    val screenHeight = LocalWindowInfo.current.containerSize.height.toDp()
    val itemMinHeight = contentHeight() + cardHeight()

    // 행 개수 = usable height에서 몇 개 들어갈 수 있는지
    return maxOf(1, (screenHeight / itemMinHeight).toInt())
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
internal fun contentHeight(): Dp {
    return contentWidth() * 0.63f
}

@Composable
internal fun cardHeight(): Dp {
    return cardTopPreviewHeight() + cardBottomPreviewHeight()
}

@Composable
internal fun contentSpace(): Dp {
    val screenHeight = LocalWindowInfo.current.containerSize.height.toDp()
    val size = (contentSize() + 1).toFloat()
    val contentSpace = (screenHeight - (cardHeight() * size)) / size
    return contentSpace
}