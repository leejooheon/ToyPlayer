package com.jooheon.toyplayer.features.commonui.components.menu

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText


enum class DropDownMenu(val title: UiText) {
    PlaylistChangeName(UiText.StringResource(Strings.action_change_name)),
    PlaylistDelete(UiText.StringResource(Strings.action_delete)),

    PlaylistMediaItemDelete(UiText.StringResource(Strings.action_delete)),

    MediaItemAddToPlayingQueue(UiText.StringResource(Strings.action_add_to_playing_queue)),
    MediaItemAddToPlaylist(UiText.StringResource(Strings.action_add_playlist)),
    MediaItemDetails(UiText.StringResource(Strings.action_details)),
    ;

    companion object {
        val playlistMenuItems = listOf(PlaylistDelete, PlaylistChangeName)
        val playlistMediaItemMenuItems = listOf(PlaylistMediaItemDelete, MediaItemDetails)
        val mediaMenuItems = listOf(MediaItemAddToPlayingQueue, MediaItemAddToPlaylist, MediaItemDetails)
    }
}

@Composable
fun DropDownMenu(
    expanded: Boolean,
    menus: List<DropDownMenu>,
    onDismissRequest: () -> Unit,
    onClick: (DropDownMenu) -> Unit
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
        DropDownMenu(
            expanded = true,
            menus = DropDownMenu.playlistMenuItems,
            onDismissRequest = {},
            onClick = {}
        )
    }
}