package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.components.PlaylistMediaColumn
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.model.MusicPlaylistScreenState
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicPlaylistScreen(
    musicPlaylistScreenState: MusicPlaylistScreenState,
    onMusicPlaylistScreenEvent: (MusicPlaylistScreenEvent) -> Unit,
    onMusicPlaylistItemEvent: (MusicPlaylistItemEvent) -> Unit,

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
    PreviewTheme(false) {
        MusicPlaylistScreen(
            musicPlaylistScreenState = MusicPlaylistScreenState.default,
            onMusicPlaylistScreenEvent = {},
            onMusicPlaylistItemEvent = {},

            musicPlayerState = MusicPlayerState.default,
            onMusicPlayerEvent = {},
        )
    }
}