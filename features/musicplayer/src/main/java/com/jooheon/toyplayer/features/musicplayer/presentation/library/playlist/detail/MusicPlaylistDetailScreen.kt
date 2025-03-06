package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.strings.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaDetailHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaItemSmallNoImage
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.components.MusicPlaylistDetailHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenState
import java.lang.Float
import kotlin.Int
import kotlin.OptIn
import kotlin.Unit
import kotlin.let
import kotlin.math.max
import kotlin.with

@Composable
fun MusicPlaylistDetailScreen(
    onBackClick: () -> Unit,
    navigate: (ScreenNavigation.Music) -> Unit,
    playlistId: Int,
    viewModel: MusicPlaylistDetailScreenViewModel = hiltViewModel()
) {
    viewModel.init(playlistId)
    viewModel.navigateTo.observeWithLifecycle { route ->
        if(route is ScreenNavigation.Back) {
            onBackClick.invoke()
        } else {
            (route as? ScreenNavigation.Music)?.let {
                navigate.invoke(route)
            }
        }
    }
    val screenState by viewModel.musicPlaylistDetailScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicPlaylistDetailScreen(
        musicPlaylistDetailScreenState = screenState,
        onMusicPlaylistScreenEvent = viewModel::dispatch,
        onMediaDropDownMenuEvent = {},

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MusicPlaylistDetailScreen(
    musicPlaylistDetailScreenState: MusicPlaylistDetailScreenState,
    onMusicPlaylistScreenEvent: (MusicPlaylistDetailScreenEvent) -> Unit,
    onMediaDropDownMenuEvent: (SongItemEvent) -> Unit,

    musicPlayerState: MusicPlayerState,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = musicPlaylistDetailScreenState.playlist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { onMusicPlaylistScreenEvent(MusicPlaylistDetailScreenEvent.OnBackClick) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "back",
                    )
                }
            }
        )
        MediaSwipeableLayout(
            musicPlayerState = musicPlayerState,
            swipeableState = swipeableState,
            swipeAreaHeight = swipeAreaHeight,
            motionProgress = motionProgress,
            onEvent = onMusicPlayerEvent,
            content = {
                PlaylistDetailMediaColumn(
                    listState = rememberLazyListState(),
                    playlist = musicPlaylistDetailScreenState.playlist,
                    onEvent = onMusicPlaylistScreenEvent,
                    onMusicPlayerEvent = onMusicPlayerEvent,
                    onDropDownEvent = onMediaDropDownMenuEvent,
                )
            }
        )
    }
}

@Composable
private fun PlaylistDetailMediaColumn(
    listState: LazyListState,
    playlist: Playlist,
    onEvent: (MusicPlaylistDetailScreenEvent) -> Unit,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
    onDropDownEvent: (SongItemEvent) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            item {
                MusicPlaylistDetailHeader(
                    playlist = playlist,
                    onPlayAllClick = {
                        onMusicPlayerEvent(
                            MusicPlayerEvent.OnEnqueue(
                                songs = playlist.songs,
                                shuffle = it,
                                playWhenReady = true
                            )
                        )
                    },
                )
            }

            item {
                MediaDetailHeader(
                    count = playlist.songs.size
                )
            }

            itemsIndexed(
                items = playlist.songs,
            ) { index, song ->
                MediaItemSmallNoImage(
                    trackNumber = index + 1,
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    duration = MusicUtil.toReadableDurationString(song.duration),
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.playlistMediaItems),
                    onItemClick = { onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(song))},
                    onDropDownMenuClick = {
                        val event = MusicDropDownMenuState.indexToEvent(index, song)
                        onDropDownEvent(event)
                    }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    )
}

@Preview
@Composable
private fun MusicPlaylistDetailScreenPreview() {
    ToyPlayerTheme {
        MusicPlaylistDetailScreen(
            musicPlaylistDetailScreenState = MusicPlaylistDetailScreenState.default.copy(
                playlist = Playlist.default.copy(
                    name = UiText.StringResource(R.string.placeholder_long).asString()
                )
            ),
            onMusicPlaylistScreenEvent = {},
            onMediaDropDownMenuEvent = {},

            musicPlayerState = MusicPlayerState.default,
            onMusicPlayerEvent = {},
        )
    }
}
