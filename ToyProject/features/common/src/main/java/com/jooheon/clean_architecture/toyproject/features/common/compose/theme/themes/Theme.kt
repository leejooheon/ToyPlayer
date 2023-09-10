package com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.Shapes
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.Typography
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.AlphaNearOpaque
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.CustomColors
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.LocalCustomColors
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.pallette.DarkColorPalette
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.pallette.DarkColorScheme
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.pallette.LightColorPalette
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.colors.pallette.LightColorScheme

@Composable
fun ApplicationTheme(
    theme: Entity.SupportThemes,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(theme)
    val customColors = when(theme) {
        Entity.SupportThemes.LIGHT,
        Entity.SupportThemes.DYNAMIC_LIGHT -> {
            LightColorPalette.update(colorScheme)
        }

        Entity.SupportThemes.DARK,
        Entity.SupportThemes.DYNAMIC_DARK -> {
            DarkColorPalette.update(colorScheme)
        }

        Entity.SupportThemes.AUTO -> {
            if(isSystemInDarkTheme()) {
                DarkColorPalette.update(colorScheme)
            } else {
                LightColorPalette.update(colorScheme)
            }
        }
    }

    val sysUiController = rememberSystemUiController()
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = customColors.material3Colors.background.copy(alpha = AlphaNearOpaque)
        )
    }

    ProvideCustomColors(customColors) {
        MaterialTheme(
            colorScheme = customColors.material3Colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
@Composable
fun getColorScheme(theme: Entity.SupportThemes): ColorScheme {
    val supportDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // 12 이상일때
    val colorScheme = when(theme) {
        Entity.SupportThemes.AUTO -> parseColorScheme(
            supportDynamicColor = supportDynamicColor,
            isDark = isSystemInDarkTheme()
        )
        Entity.SupportThemes.LIGHT -> parseColorScheme(
            supportDynamicColor = false,
            isDark = false
        )
        Entity.SupportThemes.DARK -> parseColorScheme(
            supportDynamicColor = false,
            isDark = true
        )

        Entity.SupportThemes.DYNAMIC_LIGHT -> parseColorScheme(
            supportDynamicColor = supportDynamicColor,
            isDark = false
        )
        Entity.SupportThemes.DYNAMIC_DARK -> parseColorScheme(
            supportDynamicColor = supportDynamicColor,
            isDark = true
        )
    }
    return colorScheme
}
@Composable
private fun parseColorScheme(supportDynamicColor: Boolean, isDark: Boolean): ColorScheme {
    val colorScheme = if(isDark) {
        if(supportDynamicColor) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            DarkColorScheme
        }
    } else {
        if(supportDynamicColor) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            LightColorScheme
        }
    }
    return colorScheme
}

@Composable
fun ProvideCustomColors(
    colors: CustomColors,
    content: @Composable () -> Unit
) {
    val colorPalette = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colors]
        // provided, and overwrite the values set in it.
        colors.copy()
    }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalCustomColors provides colorPalette, content = content)
}
