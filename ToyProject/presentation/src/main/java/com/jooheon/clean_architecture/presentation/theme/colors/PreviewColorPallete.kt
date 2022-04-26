package com.jooheon.clean_architecture.presentation.view.temp

import androidx.compose.runtime.Composable
import com.jooheon.clean_architecture.presentation.theme.colors.DarkColorPalette
import com.jooheon.clean_architecture.presentation.theme.colors.LightColorPalette

@Composable
fun PreviewColorPallete(dark:Boolean = false) = when(dark) {
    true -> DarkColorPalette
    false -> LightColorPalette
}