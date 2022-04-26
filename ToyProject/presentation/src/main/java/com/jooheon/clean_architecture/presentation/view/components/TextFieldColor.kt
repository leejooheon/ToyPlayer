package com.jooheon.clean_architecture.presentation.view.components

import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme

@Composable
fun outlinedTextFieldColor() : TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        textColor = CustomTheme.colors.textPrimary,
        focusedBorderColor = CustomTheme.colors.brandSecondary,
        unfocusedBorderColor = CustomTheme.colors.brand,
        focusedLabelColor = CustomTheme.colors.brandSecondary,
        unfocusedLabelColor = CustomTheme.colors.brand
    )
}