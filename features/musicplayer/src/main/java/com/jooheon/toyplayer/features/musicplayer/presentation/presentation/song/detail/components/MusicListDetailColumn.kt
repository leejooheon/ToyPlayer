package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog.MediaDropDownMenuDialogEvents
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail.model.MusicListDetailScreenState


@Composable
internal fun MusicListDetailComponent(
    playlists: List<Playlist>,
    songList: List<Song>,
    listState: LazyListState,
    songMediaColumnItemType: Boolean,

    onSongClick: (song: Song) -> Unit,
    onMediaItemEvent: (SongItemEvent) -> Unit,

    modifier: Modifier,
) {
    var songItemEventState by remember {
        mutableStateOf<SongItemEvent>(SongItemEvent.Placeholder)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier,
    ) {
        items(
            items = songList,
            key = { song: Song -> song.hashCode() }
        ) { song ->
            MusicListDetailColumnItem(
                song = song,
                viewType = songMediaColumnItemType,
                onMediaItemClick = { onSongClick(song) }, // FIXME
                onMediaItemEvent = { songItemEventState = it }
            )
        }
    }

    MediaDropDownMenuDialogEvents(
        playlists = playlists,
        event = songItemEventState,
        onDismiss = { songItemEventState = SongItemEvent.Placeholder },
        onRedirectEvent = onMediaItemEvent
    )
}
@Preview
@Composable
private fun MusicSongMediaColumnPreview() {
    PreviewTheme(true) {
        Column(
            modifier = Modifier.width(400.dp)
        ) {

            MusicListDetailComponent(
                playlists = MusicListDetailScreenState.default.playlists,
                songList = MusicListDetailScreenState.default.songList,
                listState = rememberLazyListState(),
                songMediaColumnItemType = true,
                onSongClick = {},
                onMediaItemEvent = {},
                modifier = Modifier
            )
        }
    }
}
@Preview
@Composable
private fun MusicSongMediaColumnPreview2() {
    PreviewTheme(true) {
        Column(
            modifier = Modifier.width(400.dp)
        ) {
            MusicListDetailComponent(
                playlists = MusicListDetailScreenState.default.playlists,
                songList = MusicListDetailScreenState.default.songList,
                listState = rememberLazyListState(),
                songMediaColumnItemType = false,
                onSongClick = {},
                onMediaItemEvent = {},
                modifier = Modifier
            )
        }
    }
}