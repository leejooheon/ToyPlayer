package com.jooheon.toyplayer.features.common.compose.components

import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private const val DividerAlpha = 0.12f
@Composable
fun CustomDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}