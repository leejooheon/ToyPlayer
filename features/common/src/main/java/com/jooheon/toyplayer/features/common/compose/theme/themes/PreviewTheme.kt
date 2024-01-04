package com.jooheon.toyplayer.features.common.compose.theme.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.jooheon.toyplayer.features.common.compose.theme.Shapes
import com.jooheon.toyplayer.features.common.compose.theme.Typography
import com.jooheon.toyplayer.features.common.compose.theme.colors.pallette.previewColorPallete


@Composable
fun PreviewTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val pallette = previewColorPallete(darkTheme)
    ProvideCustomColors(pallette) {
        MaterialTheme(
            colorScheme = pallette.material3Colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}