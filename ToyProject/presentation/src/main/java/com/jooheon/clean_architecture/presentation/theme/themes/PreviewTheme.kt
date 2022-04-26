package com.jooheon.clean_architecture.presentation.theme.themes

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.jooheon.clean_architecture.presentation.theme.Shapes
import com.jooheon.clean_architecture.presentation.theme.Typography
import com.jooheon.clean_architecture.presentation.theme.colors.LocalCustomColors
import com.jooheon.clean_architecture.presentation.theme.colors.pallette.debugColors
import com.jooheon.clean_architecture.presentation.view.temp.previewColorPallete


@Composable
fun PreviewTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val pallette = previewColorPallete(darkTheme)

    val colorPalette = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colors]
        // provided, and overwrite the values set in it.
        pallette
    }

    colorPalette.update(pallette)
    CompositionLocalProvider(LocalCustomColors provides colorPalette, content = content)
}