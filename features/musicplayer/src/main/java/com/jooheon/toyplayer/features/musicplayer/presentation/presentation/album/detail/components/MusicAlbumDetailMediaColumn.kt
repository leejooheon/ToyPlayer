package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.model.MusicAlbumDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaDetailHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog.MediaDropDownMenuDialogEvents
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent


@Composable
internal fun MusicAlbumDetailMediaColumn(
    musicAlbumDetailScreenState: MusicAlbumDetailScreenState,
    listState: LazyListState = rememberLazyListState(),
    onEvent: (MusicAlbumDetailScreenEvent) -> Unit,
    onMediaItemEvent: (MusicMediaItemEvent) -> Unit,
) {
    val album = musicAlbumDetailScreenState.album
    val playlists = musicAlbumDetailScreenState.playlists

    var musicMediaItemEventState by remember {
        mutableStateOf<MusicMediaItemEvent>(MusicMediaItemEvent.Placeholder)
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                MusicAlbumDetailHeader(
                    album = album,
                    onActionPlayAll = {
                        onEvent(
                            MusicAlbumDetailScreenEvent.OnActionPlayAll(
                                album = album,
                                shuffle = it
                            )
                        )
                    }
                )
                MediaDetailHeader(
                    count = album.songs.size
                )
            }

            items(
                items = album.songs,
                key = { song: Song -> song.hashCode() }
            ) {song ->
                MediaItemSmallNoImage(
                    trackNumber = song.trackNumber,
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    duration = MusicUtil.toReadableDurationString(song.duration),
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.mediaItems),
                    onItemClick = { onEvent(MusicAlbumDetailScreenEvent.OnSongClick(song)) },
                    onDropDownMenuClick = {
                        val event = MusicDropDownMenuState.indexToEvent(it, song)
                        musicMediaItemEventState = event
                    }
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }

        MediaDropDownMenuDialogEvents(
            playlists = playlists,
            event = musicMediaItemEventState,
            onDismiss = { musicMediaItemEventState = MusicMediaItemEvent.Placeholder },
            onRedirectEvent = onMediaItemEvent
        )
    }
}
@Preview
@Composable
private fun MusicAlbumDetailMediaColumnPreview() {
    PreviewTheme(true) {
        MusicAlbumDetailMediaColumn(
            musicAlbumDetailScreenState = MusicAlbumDetailScreenState.default,
            listState = rememberLazyListState(),
            onEvent = {},
            onMediaItemEvent = {},
        )
    }
}