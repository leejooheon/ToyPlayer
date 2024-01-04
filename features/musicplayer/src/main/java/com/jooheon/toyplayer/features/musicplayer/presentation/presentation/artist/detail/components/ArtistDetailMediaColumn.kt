package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model.MusicArtistDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaDetailHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dialog.MediaDropDownMenuDialogEvents
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent


@Composable
internal fun ArtistDetailMediaColumn(
    musicArtistDetailScreenState: MusicArtistDetailScreenState,
    listState: LazyListState = rememberLazyListState(),
    onEvent: (MusicArtistDetailScreenEvent) -> Unit,
    onMediaItemEvent: (MusicMediaItemEvent) -> Unit,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val artist = musicArtistDetailScreenState.artist
    val playlists = musicArtistDetailScreenState.playlists

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
            items(
                items = artist.albums,
                key = { album: Album -> album.hashCode() }
            ) { album ->
                ArtistDetailAlbumItem(
                    imageUrl = album.songs.firstOrNull()?.imageUrl.defaultEmpty(),
                    title = album.name,
                    subTitle = album.artist,
                    onItemClick = { onEvent(MusicArtistDetailScreenEvent.OnAlbumClick(album)) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                MediaDetailHeader(count = album.songs.size)

                album.songs.forEach { song ->
                    MediaItemSmallNoImage(
                        trackNumber = song.trackNumber,
                        title = song.title,
                        subTitle = "${song.artist} • ${song.album}",
                        duration = MusicUtil.toReadableDurationString(song.duration),
                        dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.mediaItems),
                        onItemClick = { onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(song)) },
                        onDropDownMenuClick = {
                            val event = MusicDropDownMenuState.indexToEvent(it, song)
                            musicMediaItemEventState = event
                        }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
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
private fun ArtistDetailMediaColumnPreview() {
    PreviewTheme(true) {
        ArtistDetailMediaColumn(
            musicArtistDetailScreenState = MusicArtistDetailScreenState.default,
            listState = rememberLazyListState(),
            onEvent = {},
            onMediaItemEvent = {},
            onMusicPlayerEvent = {},
        )
    }
}