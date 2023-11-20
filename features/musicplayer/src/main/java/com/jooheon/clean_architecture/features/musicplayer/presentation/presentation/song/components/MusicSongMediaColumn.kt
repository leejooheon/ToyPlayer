package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.song.components

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
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dialog.MediaDropDownMenuDialogEvents
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState


@Composable
internal fun MusicSongMediaColumn(
    musicSongScreenState: MusicSongScreenState,
    listState: LazyListState,
    songMediaColumnItemType: Boolean,

    onSongClick: (song: Song) -> Unit,
    onMediaItemEvent: (MusicMediaItemEvent) -> Unit,

    modifier: Modifier,
) {
    val playlists = musicSongScreenState.playlists

    var musicMediaItemEventState by remember {
        mutableStateOf<MusicMediaItemEvent>(MusicMediaItemEvent.Placeholder)
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier,
    ) {
        items(
            items = musicSongScreenState.songList,
            key = { song: Song -> song.hashCode() }
        ) { song ->
            MusicSongMediaColumnItem(
                song = song,
                viewType = songMediaColumnItemType,
                onMediaItemClick = { onSongClick(song) }, // FIXME
                onMediaItemEvent = { musicMediaItemEventState = it }
            )
        }
    }

    MediaDropDownMenuDialogEvents(
        playlists = playlists,
        event = musicMediaItemEventState,
        onDismiss = { musicMediaItemEventState = MusicMediaItemEvent.Placeholder },
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

            MusicSongMediaColumn(
                musicSongScreenState = MusicSongScreenState.default,
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
            MusicSongMediaColumn(
                musicSongScreenState = MusicSongScreenState.default,
                listState = rememberLazyListState(),
                songMediaColumnItemType = false,
                onSongClick = {},
                onMediaItemEvent = {},
                modifier = Modifier
            )
        }
    }
}