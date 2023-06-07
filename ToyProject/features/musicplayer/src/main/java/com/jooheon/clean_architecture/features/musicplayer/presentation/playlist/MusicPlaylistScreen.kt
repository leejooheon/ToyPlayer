package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.components.PlaylistMediaColumn
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenState
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MusicPlaylistScreen(
    musicPlaylistScreenState: MusicPlaylistScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,

    onMusicPlayerScreenEvent: (MusicPlayerScreenEvent) -> Unit,
    onMusicPlaylistScreenEvent: (MusicPlaylistScreenEvent) -> Unit,
    onMusicPlaylistItemEvent: (MusicPlaylistItemEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerScreenEvent,
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
            musicPlayerScreenState = MusicPlayerScreenState.default,

            onMusicPlaylistScreenEvent = {},
            onMusicPlayerScreenEvent = {},
            onMusicPlaylistItemEvent = {},
        )
    }
}