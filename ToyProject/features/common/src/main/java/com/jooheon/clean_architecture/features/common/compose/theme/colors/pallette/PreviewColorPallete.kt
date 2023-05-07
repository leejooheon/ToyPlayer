package com.jooheon.clean_architecture.features.common.compose.theme.colors.pallette

import androidx.compose.runtime.Composable

@Composable
internal fun previewColorPallete(darkTheme:Boolean = false) = when(darkTheme) {
    true -> DarkColorPalette
    false -> LightColorPalette
}