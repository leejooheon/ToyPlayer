package com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog

import androidx.compose.runtime.Composable
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent


@Composable
fun MediaDropDownMenuDialogEvents(
    playlists: List<Playlist>,
    event: SongItemEvent,
    onDismiss: () -> Unit,
    onRedirectEvent: (SongItemEvent) -> Unit
) {
    when(event) {
        is SongItemEvent.OnDetailsClick -> {
            MediaDetailsDialog(
                song = event.song,
                onDismiss = onDismiss
            )
        }
        is SongItemEvent.OnAddPlaylistClick -> {
            AddSongIntoPlaylistDialog(
                song = event.song,
                playlists = playlists,
                onPlaylistClick = { playlist ->
                    onRedirectEvent(SongItemEvent.OnAddPlaylistClick(event.song ,playlist))
                },
                onDismiss = onDismiss
            )
        }
        else -> {
            onRedirectEvent(event)
            onDismiss()
        }
    }
}