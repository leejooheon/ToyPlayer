package com.jooheon.toyplayer.features.settings.presentation.equalizer.component.equalizer.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun ChipWithDropdown(
    label: String,
    options: List<Pair<Int, String>>, // id, label
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(label) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val (id, title) = option
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        onOptionSelected.invoke(id)
                        expanded = false
                    }
                )
            }
        }
    }
}