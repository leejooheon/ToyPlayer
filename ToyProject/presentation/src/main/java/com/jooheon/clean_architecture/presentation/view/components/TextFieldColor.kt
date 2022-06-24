package com.jooheon.clean_architecture.presentation.view.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme

@Composable
fun outlinedTextFieldColor() : TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = MaterialTheme.colorScheme.onBackground,
        textColor = MaterialTheme.colorScheme.onBackground,
        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary
    )
}