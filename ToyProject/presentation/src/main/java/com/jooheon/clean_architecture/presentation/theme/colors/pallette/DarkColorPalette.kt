package com.jooheon.clean_architecture.presentation.theme.colors

import androidx.compose.material3.darkColorScheme
import com.jooheon.clean_architecture.presentation.theme.*

internal val DarkColorPalette = CustomColors(
    material3Colors = darkColorScheme(
        primary = DarkGreen40,
        onPrimary = Neutral0,
        inversePrimary = DarkGreen80,

        primaryContainer = DarkGreen10,
        onPrimaryContainer = DarkGreen90,

        secondary = GreenGrey30,
        onSecondary = Neutral10,

        secondaryContainer = GreenGrey10,
        onSecondaryContainer = GreenGrey90,

        tertiary = Teal40,
        onTertiary = Neutral0,

        tertiaryContainer = Teal10,
        onTertiaryContainer = Teal90,

        error = Red20,
        onError = Red80,

        errorContainer = Red30,
        onErrorContainer = Red90,

        background = Neutral10,
        onBackground = Neutral99,

        surface = Neutral10,
        onSurface = Neutral99,

        surfaceVariant = Neutral_Variant30,
        onSurfaceVariant = Neutral_Variant90,

        inverseSurface = Neutral99,
        inverseOnSurface = Neutral10,

        outline = Neutral_Variant50
    ),

    gradient6_1 = listOf(Shadow5, Ocean7, Shadow9, Ocean7, Shadow5),
    gradient6_2 = listOf(Rose11, Lavender7, Rose8, Lavender7, Rose11),
    gradient3_1 = listOf(Shadow9, Ocean7, Shadow5),
    gradient3_2 = listOf(Rose8, Lavender7, Rose11),
    gradient2_1 = listOf(Ocean3, Shadow3),
    gradient2_2 = listOf(Ocean4, Shadow2),
    gradient2_3 = listOf(Lavender3, Rose3),
    isDark = true
)
