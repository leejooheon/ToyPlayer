package com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme

@Composable
fun MusicDropDownMenu(
    expanded: Boolean,
    dropDownMenuState: MusicDropDownMenuState,
    onDismissRequest: () -> Unit,
    onClick: (index: Int) -> Unit
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

@Preview
@Composable
private fun MediaDropDownMenuPreview() {
    ToyPlayerTheme {
        MusicDropDownMenu(
            expanded = true,
            dropDownMenuState = MusicDropDownMenuState(items = MusicDropDownMenuState.playlistItems),
            onDismissRequest = {},
            onClick = {}
        )
    }
}