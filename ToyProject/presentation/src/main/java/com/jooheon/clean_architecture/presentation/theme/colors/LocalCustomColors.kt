package com.jooheon.clean_architecture.presentation.theme.colors

import androidx.compose.runtime.staticCompositionLocalOf

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColorPalette provided")
}