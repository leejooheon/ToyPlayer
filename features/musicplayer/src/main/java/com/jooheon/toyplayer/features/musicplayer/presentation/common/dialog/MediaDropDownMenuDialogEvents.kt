package com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog

import androidx.compose.runtime.Composable
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent


@Composable
fun MediaDropDownMenuDialogEvents(
    playlists: List<Playlist>,
    event: MusicMediaItemEvent,
    onDismiss: () -> Unit,
    onRedirectEvent: (MusicMediaItemEvent) -> Unit
) {
    when(event) {
        is MusicMediaItemEvent.OnDetailsClick -> {
            MediaDetailsDialog(
                song = event.song,
                onDismiss = onDismiss
            )
        }
        is MusicMediaItemEvent.OnAddPlaylistClick -> {
            AddSongIntoPlaylistDialog(
                song = event.song,
                playlists = playlists,
                onPlaylistClick = { playlist ->
                    onRedirectEvent(MusicMediaItemEvent.OnAddPlaylistClick(event.song ,playlist))
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