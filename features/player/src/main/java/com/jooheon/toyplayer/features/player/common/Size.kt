package com.jooheon.toyplayer.features.player.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.common.extension.deviceWidth
import com.jooheon.toyplayer.features.common.extension.getHeights
import com.jooheon.toyplayer.features.commonui.ext.isSystemBarVisible
import com.jooheon.toyplayer.features.commonui.ext.systemTopBarHeight
import com.jooheon.toyplayer.features.commonui.ext.toDp

@Composable
fun verticalMargin(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val verticalMargin = screenHeight * 0.05f

    return verticalMargin
}

@Composable
fun horizontalMargin(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenWidthDp.dp
    val verticalMargin = screenHeight * 0.05f

    return verticalMargin
}

@Composable
fun contentWidth(): Dp {
    val context = LocalContext.current

    val pixel = (context.deviceWidth() * 0.25).toFloat()
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

    val contentSpace = (screenHeight - (cardHeight() * 5f)) / 5f
//    Timber.d("contentSpace[$visible]: [$contentSpace, ${cardHeight()}] ([${appUsableHeight.toDp()}, ${fullHeight.toDp()}] - ${systemTopBarHeight()} = $screenHeight)")
    return contentSpace
}