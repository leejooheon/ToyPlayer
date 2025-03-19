package com.jooheon.toyplayer.features.common.compose.components.dropdown

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
    menus: List<MusicDropDownMenu>,
    onDismissRequest: () -> Unit,
    onClick: (MusicDropDownMenu) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary),
        content = {
            menus.forEach { menu ->
                DropdownMenuItem(
                    onClick = {
                        onClick(menu)
                        onDismissRequest()
                    },
                    text = {
                        Text(
                            text = menu.title.asString(),
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
            menus = MusicDropDownMenu.playlistMenuItems,
            onDismissRequest = {},
            onClick = {}
        )
    }
}