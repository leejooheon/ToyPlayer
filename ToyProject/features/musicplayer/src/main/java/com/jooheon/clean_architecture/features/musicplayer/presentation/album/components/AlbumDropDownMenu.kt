package com.jooheon.clean_architecture.features.musicplayer.presentation.album.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState

@Composable
internal fun AlbumDropDownMenu(
    expanded: Boolean,
    dropDownMenuState: MusicDropDownMenuState,
    onDismissRequest: () -> Unit,
    onClick: (Int) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary),
        content = {
            dropDownMenuState.items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        onClick(index)
                        onDismissRequest()
                    },
                    text = {
                        Text(
                            text = item.asString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    },
                )
            }
        },
    )
}