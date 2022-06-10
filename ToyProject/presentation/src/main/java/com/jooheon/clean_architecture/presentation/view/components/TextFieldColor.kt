package com.jooheon.clean_architecture.presentation.view.components

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme

@Composable
fun outlinedTextFieldColor() : TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = CustomTheme.colors.material3Colors.onBackground,
        textColor = CustomTheme.colors.material3Colors.onBackground,
        focusedBorderColor = CustomTheme.colors.material3Colors.tertiary,
        unfocusedBorderColor = CustomTheme.colors.material3Colors.onTertiary,
        focusedLabelColor = CustomTheme.colors.material3Colors.tertiary,
        unfocusedLabelColor = CustomTheme.colors.material3Colors.onTertiary
    )
}