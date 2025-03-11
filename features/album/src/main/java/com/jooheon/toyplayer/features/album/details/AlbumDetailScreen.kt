package com.jooheon.toyplayer.features.album.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.album.details.components.AlbumDetailHeader
import com.jooheon.toyplayer.features.album.details.model.AlbumDetailEvent
import com.jooheon.toyplayer.features.album.details.model.AlbumDetailUiState
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox
import com.jooheon.toyplayer.features.common.compose.components.media.MediaDetailHeader
import com.jooheon.toyplayer.features.common.compose.components.media.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.common.compose.components.media.MusicDropDownMenuState
import com.jooheon.toyplayer.features.common.utils.MusicUtil

@Composable
fun AlbumDetailScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    albumId: String,
    viewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData(context, albumId)
    }

    AlbumDetailScreenInternal(
        uiState = uiState,
        onEvent = {},
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) }
    )
}

@Composable
private fun AlbumDetailScreenInternal(
    uiState: AlbumDetailUiState,
    onEvent: (AlbumDetailEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val listState = rememberLazyListState()
    TopAppBarBox(
        title = uiState.album.name,
        onClick = onBackClick,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                AlbumDetailHeader(
                    album = uiState.album,
                    onPlayAllClick = {
//                        onMusicPlayerEvent(
//                            MusicPlayerEvent.OnEnqueue(
//                                songs = album.songs,
//                                shuffle = it,
//                                playWhenReady = true
//                            )
//                        )
                    }
                )
                MediaDetailHeader(
                    count = uiState.album.songs.size
                )
            }

            items(
                items = uiState.album.songs,
                key = { song: Song -> song.hashCode() }
            ) { song ->
                MediaItemSmallNoImage(
                    trackNumber = song.trackNumber,
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    duration = MusicUtil.toReadableDurationString(song.duration),
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.mediaItems),
                    onItemClick = {
//                        onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(song))
                                  },
                    onDropDownMenuClick = {
//                        val event = MusicDropDownMenuState.indexToEvent(it, song)
//                        songItemEventState = event
                    }
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }

//        MediaDropDownMenuDialogEvents(
//            playlists = playlists,
//            event = songItemEventState,
//            onDismiss = { songItemEventState = SongItemEvent.Placeholder },
//            onRedirectEvent = onMediaItemEvent
//        )
    }
}
@Preview
@Composable
private fun MusicAlbumDetailMediaColumnPreview() {
    ToyPlayerTheme {
        AlbumDetailScreenInternal(
            uiState = AlbumDetailUiState.preview,
            onEvent = {},
            onBackClick = {},
        )
    }
}