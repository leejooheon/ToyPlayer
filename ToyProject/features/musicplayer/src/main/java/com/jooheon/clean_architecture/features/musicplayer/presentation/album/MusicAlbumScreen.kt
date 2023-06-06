@file:OptIn(ExperimentalMaterial3Api::class)

package com.jooheon.clean_architecture.features.musicplayer.presentation.album

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.components.AlbumMediaColumn
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenState
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicAlbumScreen(
    musicAlbumState: MusicAlbumScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,
    onMusicAlbumEvent: (MusicAlbumScreenEvent) -> Unit,
    onMusicPlayerScreenEvent: (MusicPlayerScreenEvent) -> Unit,
) {

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val listState = rememberLazyGridState()

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerScreenEvent,
        content = {
            AlbumMediaColumn(
                albumList = musicAlbumState.albums,
                listState = listState,
                onItemClick = { onMusicAlbumEvent(MusicAlbumScreenEvent.OnAlbumItemClick(it)) }
            )
        }
    )
}


@Preview
@Composable
private fun MusicAlbumScreenPreview() {
    PreviewTheme(false) {
        MusicAlbumScreen(
            musicAlbumState = MusicAlbumScreenState.default,
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onMusicAlbumEvent = {},
            onMusicPlayerScreenEvent = {},
        )
    }
}