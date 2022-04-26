package com.jooheon.clean_architecture.presentation.view.temp

import androidx.compose.runtime.Composable
import com.jooheon.clean_architecture.presentation.theme.colors.DarkColorPalette
import com.jooheon.clean_architecture.presentation.theme.colors.LightColorPalette

@Composable
fun previewColorPallete(darkTheme:Boolean = false) = when(darkTheme) {
    true -> DarkColorPalette
    false -> LightColorPalette
}