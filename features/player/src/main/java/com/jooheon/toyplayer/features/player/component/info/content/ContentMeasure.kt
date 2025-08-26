package com.jooheon.toyplayer.features.player.component.info.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.ext.toDp
import com.jooheon.toyplayer.features.player.common.contentHeight
import com.jooheon.toyplayer.features.player.common.contentSpace

@Composable
internal fun wholeContentSectionHeight(): Dp {
    val sectionTitle = sectionTitleHeight()
    val cardBottomPreviewHeight = cardBottomPreviewHeight()
    return sectionTitle + cardBottomPreviewHeight + contentSpace()
}

@Composable
internal fun sectionTitleHeight(): Dp {
    val titleHeight = textHeight(
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1
    )

    return titleHeight + 8.dp + 8.dp // top, bottom padding
}

@Composable
internal fun textHeight(
    style: TextStyle,
    maxLines: Int,
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = UiText.StringResource(Strings.lorem_ipsum).asString(),
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        constraints = Constraints.fixedWidth(1)
    )
    val textHeight = textLayoutResult.size.height
    return textHeight.toDp()
}

@Composable
internal fun cardBottomPreviewHeight(): Dp {
    val contentHeight =  contentHeight() * 0.4f
    val contentTitleHeight = textHeight(
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
    )

    return contentHeight + contentTitleHeight
}

@Composable
internal fun cardTopPreviewHeight(): Dp { // 화면 아래에 보이는거
    val sectionTitleHeight = textHeight(
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
    )
    val titleTextPadding = (8.dp) * 2 // ContentItem.kt padding
    val height = sectionTitleHeight + titleTextPadding
    val result = (contentHeight() * 0.6f) + height
    return result
}