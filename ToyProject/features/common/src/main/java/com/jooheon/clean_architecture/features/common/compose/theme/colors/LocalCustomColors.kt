package com.jooheon.clean_architecture.features.common.compose.theme.colors

import androidx.compose.runtime.staticCompositionLocalOf

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("No CustomColorPalette provided")
}