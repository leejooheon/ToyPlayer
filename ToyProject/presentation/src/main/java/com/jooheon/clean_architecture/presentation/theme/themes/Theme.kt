package com.jooheon.clean_architecture.presentation.theme.themes

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jooheon.clean_architecture.presentation.theme.AlphaNearOpaque
import com.jooheon.clean_architecture.presentation.theme.Shapes
import com.jooheon.clean_architecture.presentation.theme.Typography
import com.jooheon.clean_architecture.presentation.theme.colors.CustomColors
import com.jooheon.clean_architecture.presentation.theme.colors.DarkColorPalette
import com.jooheon.clean_architecture.presentation.theme.colors.LightColorPalette
import com.jooheon.clean_architecture.presentation.theme.colors.LocalCustomColors

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // 12 이상일때
    val customColors = when {
        useDynamicColors && darkTheme -> DarkColorPalette.update(dynamicDarkColorScheme(LocalContext.current))
        useDynamicColors && !darkTheme -> LightColorPalette.update(dynamicLightColorScheme(LocalContext.current))
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
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
