package com.jooheon.clean_architecture.toyproject.features.common.compose.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun outlinedTextFieldColor() : TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        cursorColor = MaterialTheme.colorScheme.onSecondary,
        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
    )
}