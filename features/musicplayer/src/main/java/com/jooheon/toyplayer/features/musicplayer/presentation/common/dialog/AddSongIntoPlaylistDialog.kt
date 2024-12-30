package com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.components.MusicPlaylistColumnItem
import com.jooheon.toyplayer.core.strings.UiText


@Composable
fun AddSongIntoPlaylistDialog(
    song: Song,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
) {
    if(song == Song.default) return

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
            ) {
                AddSongPlaylistDialogHeader()
                LazyColumn(
                    modifier = Modifier,
                ) {
                    items(
                        items = playlists,
                        key = { playlist: Playlist -> playlist.hashCode() }
                    ) { playlist ->
                        MusicPlaylistColumnItem(
                            playlist = playlist,
                            showContextualMenu = false,
                            onItemClick = {
                                onPlaylistClick(playlist)
                                onDismiss()
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = UiText.StringResource(R.string.cancel).asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
    )
}

@Composable
private fun AddSongPlaylistDialogHeader() {
    Column {
        Text(
            text = UiText.StringResource(R.string.action_add_playlist).asString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
private fun AddSongIntoPlaylistDialogPreview() {
    val playlists = mutableListOf<Playlist>()
    repeat(10) {
        playlists.add(
            Playlist.default.copy(
                name = "name - ${it}"
            )
        )
    }
    ToyPlayerTheme {
        AddSongIntoPlaylistDialog(
            song = Song.default.copy(audioId =  1234L),
            playlists = playlists,
            onDismiss = {},
            onPlaylistClick = {},
        )
    }
}