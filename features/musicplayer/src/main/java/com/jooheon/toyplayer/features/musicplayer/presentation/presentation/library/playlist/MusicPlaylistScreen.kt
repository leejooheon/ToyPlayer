package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.PlaylistEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.components.PlaylistMediaColumn
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.model.MusicPlaylistScreenState
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import java.lang.Float
import kotlin.math.max

@Composable
fun MusicPlaylistScreen(
    navController: NavController,
    viewModel: MusicPlaylistScreenViewModel = hiltViewModel()
) {
    viewModel.navigateTo.observeWithLifecycle {
        navController.navigate(it)
    }
    val state by viewModel.musicPlaylistScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicPlaylistScreen(
        musicPlaylistScreenState = state,
        onMusicPlaylistScreenEvent = viewModel::dispatch,
        onMusicPlaylistItemEvent = viewModel::onPlaylistEvent,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MusicPlaylistScreen(
    musicPlaylistScreenState: MusicPlaylistScreenState,
    onMusicPlaylistScreenEvent: (MusicPlaylistScreenEvent) -> Unit,
    onMusicPlaylistItemEvent: (PlaylistEvent) -> Unit,

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

    MediaSwipeableLayout(
        musicPlayerState = musicPlayerState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerEvent,
        content = {
            PlaylistMediaColumn(
                listState = rememberLazyListState(),
                playlists = musicPlaylistScreenState.playlists ,
                onItemClick = { onMusicPlaylistScreenEvent(MusicPlaylistScreenEvent.OnPlaylistClick(it)) },
                onAddPlaylistClick = { onMusicPlaylistScreenEvent(MusicPlaylistScreenEvent.OnAddPlaylist(it)) },
                onDropDownMenuClick = { index, playlist ->
                    val event = MusicDropDownMenuState.indexToEvent(index, playlist)
                    onMusicPlaylistItemEvent(event)
                }
            )
        }
    )
}

@Preview
@Composable
private fun MusicPlaylistScreenPreview() {
    ToyPlayerTheme {
        MusicPlaylistScreen(
            musicPlaylistScreenState = MusicPlaylistScreenState.default,
            onMusicPlaylistScreenEvent = {},
            onMusicPlaylistItemEvent = {},

            musicPlayerState = MusicPlayerState.default,
            onMusicPlayerEvent = {},
        )
    }
}