package com.jooheon.clean_architecture.presentation.theme.colors.pallette

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import com.jooheon.clean_architecture.presentation.theme.Neutral7

fun debugColors(
    darkTheme: Boolean,
    debugColor: Color = Neutral7
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !darkTheme
)