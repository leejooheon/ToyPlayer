package com.jooheon.clean_architecture.features.common.compose.theme.themes

import androidx.compose.runtime.Composable
import com.jooheon.clean_architecture.features.common.compose.theme.colors.CustomColors
import com.jooheon.clean_architecture.features.common.compose.theme.colors.LocalCustomColors

object CustomTheme {
    val colors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}