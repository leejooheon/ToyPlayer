package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog.MusicPlaylistDialog
import com.jooheon.toyplayer.core.resources.UiText


@Composable
internal fun MusicPlaylistHeader(
    onAddPlaylistClick: (String) -> Unit,
) {
    var playlistDialogState by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {  },
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = UiText.StringResource(R.string.option_see_more).asString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                    )
                }
            }
        )

        IconButton(
            onClick = { playlistDialogState = true},
            content = {
                Icon(
                    imageVector = Icons.Filled.PlaylistAdd,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = UiText.StringResource(R.string.option_add_playlist).asString()
                )
            }
        )
    }

    MusicPlaylistDialog(
        openDialog = playlistDialogState,
        title = UiText.StringResource(R.string.dialog_new_playlist).asString(),
        name = "",
        onConfirmButtonClicked = onAddPlaylistClick,
        onDismiss = { playlistDialogState = false }
    )
}
@Preview
@Composable
private fun MusicPlaylistHeaderPreview() {
    ToyPlayerTheme {
        MusicPlaylistHeader(
            onAddPlaylistClick = {}
        )
    }
}