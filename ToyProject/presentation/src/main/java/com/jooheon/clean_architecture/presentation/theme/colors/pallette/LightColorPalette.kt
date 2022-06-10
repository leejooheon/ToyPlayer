package com.jooheon.clean_architecture.presentation.theme.colors

import androidx.compose.material3.lightColorScheme
import com.jooheon.clean_architecture.presentation.theme.*

internal val LightColorPalette = CustomColors(
    material3Colors = lightColorScheme(
        primary = DarkGreen80,
        onPrimary = Neutral7,
        inversePrimary = DarkGreen20,

        primaryContainer = DarkGreen90,
        onPrimaryContainer = DarkGreen10,

        secondary = GreenGrey80,
        onSecondary = Neutral7,

        secondaryContainer = GreenGrey90,
        onSecondaryContainer = GreenGrey10,

        tertiary = Teal60,
        onTertiary = Neutral0,

        tertiaryContainer = Teal90,
        onTertiaryContainer = Teal10,

        error = Red80,
        onError = Red20,

        errorContainer = Red90,
        onErrorContainer = Red30,

        background = Neutral99,
        onBackground = Neutral10,

        surface = Neutral99,
        onSurface = Neutral10,

        surfaceVariant = Neutral_Variant90,
        onSurfaceVariant = Neutral_Variant30,

        inverseSurface = Neutral10,
        inverseOnSurface = Neutral99,

        outline = Neutral_Variant50
    ),
    gradient6_1 = listOf(Shadow4, Ocean3, Shadow2, Ocean3, Shadow4),
    gradient6_2 = listOf(Rose4, Lavender3, Rose2, Lavender3, Rose4),
    gradient3_1 = listOf(Shadow2, Ocean3, Shadow4),
    gradient3_2 = listOf(Rose2, Lavender3, Rose4),
    gradient2_1 = listOf(Shadow4, Shadow11),
    gradient2_2 = listOf(Ocean3, Shadow3),
    gradient2_3 = listOf(Lavender3, Rose2),
    isDark = false
)