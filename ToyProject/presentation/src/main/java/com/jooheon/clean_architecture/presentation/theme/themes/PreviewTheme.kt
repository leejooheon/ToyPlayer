package com.jooheon.clean_architecture.presentation.theme.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.jooheon.clean_architecture.presentation.theme.Shapes
import com.jooheon.clean_architecture.presentation.theme.Typography
import com.jooheon.clean_architecture.presentation.view.temp.previewColorPallete


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