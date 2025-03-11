package com.jooheon.toyplayer.features.artist.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
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
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.features.artist.details.component.ArtistDetailAlbumItem
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailEvent
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailUiState
import com.jooheon.toyplayer.features.common.compose.components.TopAppBarBox
import com.jooheon.toyplayer.features.common.compose.components.media.MediaDetailHeader
import com.jooheon.toyplayer.features.common.compose.components.media.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.common.compose.components.media.MusicDropDownMenuState
import com.jooheon.toyplayer.features.common.utils.MusicUtil

@Composable
fun ArtistDetailScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    artistId: String,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.loadData(context, artistId)
    }

    ArtistDetailScreenInternal(
        uiState = uiState,
        onEvent = {},
        onBackClick = {
            navigateTo.invoke(ScreenNavigation.Back)
        }
    )
}

@Composable
private fun ArtistDetailScreenInternal(
    uiState: ArtistDetailUiState,
    onEvent: (ArtistDetailEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val listState = rememberLazyListState()

    TopAppBarBox(
        title = uiState.artist.name,
        onClick = onBackClick,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.artist.albums,
                    key = { album: Album -> album.hashCode() }
                ) { album ->
                    ArtistDetailAlbumItem(
                        imageUrl = album.songs.firstOrNull()?.imageUrl.defaultEmpty(),
                        title = album.name,
                        subTitle = album.artist,
                        onItemClick = {
//                            onEvent(MusicArtistDetailScreenEvent.OnAlbumClick(album))
                                      },
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
                            onItemClick = {
//                                onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(song))
                                          },
                            onDropDownMenuClick = {
//                                val event = MusicDropDownMenuState.indexToEvent(it, song)
//                                songItemEventState = event
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewArtistDetailScreen() {
    ToyPlayerTheme {
        ArtistDetailScreenInternal(
            uiState = ArtistDetailUiState.default,
            onEvent = {},
            onBackClick = {},
        )
    }
}