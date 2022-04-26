package com.jooheon.clean_architecture.presentation.theme.themes

import androidx.compose.runtime.Composable
import com.jooheon.clean_architecture.presentation.theme.colors.CustomColors
import com.jooheon.clean_architecture.presentation.theme.colors.LocalCustomColors

object CustomTheme {
    val colors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}