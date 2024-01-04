package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme


@Composable
internal fun PlaylistMediaColumn(
    listState: LazyListState,
    playlists: List<Playlist>,
    onItemClick: (Playlist) -> Unit,
    onAddPlaylistClick: (String) -> Unit,
    onDropDownMenuClick: (Int, Playlist) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 16.dp
        ),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            item {
                MusicPlaylistHeader(
                    onAddPlaylistClick = onAddPlaylistClick
                )
            }

            items(
                items = playlists,
                key = { playlist: Playlist -> playlist.hashCode() }
            ) { playlist ->
                MusicPlaylistColumnItem(
                    playlist = playlist,
                    showContextualMenu = playlist.id != Playlist.PlayingQueuePlaylistId,
                    onItemClick = { onItemClick(playlist) },
                    onDropDownMenuClick = onDropDownMenuClick,
                )
            }
        }
    )
}
@Preview
@Composable
private fun PlaylistMediaColumnPreview() {
    PreviewTheme(true) {
        PlaylistMediaColumn(
            listState = rememberLazyListState(),
            playlists = listOf(Playlist.default),
            onItemClick = {},
            onAddPlaylistClick = {},
            onDropDownMenuClick = { _, _ -> }
        )
    }
}