
package com.jooheon.toyplayer.features.musicplayer.presentation.album

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.musicplayer.presentation.album.components.AlbumMediaColumn
import com.jooheon.toyplayer.features.musicplayer.presentation.album.components.AlbumMediaHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import java.lang.Float
import kotlin.OptIn
import kotlin.Unit
import kotlin.math.max
import kotlin.with


@Composable
fun MusicAlbumScreen(
    navigate: (ScreenNavigation) -> Unit,
    viewModel: MusicAlbumScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.sortType.observeWithLifecycle {
        viewModel.loadData(context, it)
    }

    val screenState by viewModel.musicAlbumScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicAlbumScreen(
        musicAlbumState = screenState,
        onMusicAlbumEvent = viewModel::dispatch,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MusicAlbumScreen(
    musicAlbumState: MusicAlbumScreenState,
    onMusicAlbumEvent: (MusicAlbumScreenEvent) -> Unit,

    musicPlayerState: MusicPlayerState,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
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
        musicPlayerState = musicPlayerState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerEvent,
        content = {
            AlbumMediaHeader(
                onDropDownMenuClick = {
                    val sortType = MusicAlbumScreenViewModel.AlbumSortType.entries[it]
                    onMusicAlbumEvent(MusicAlbumScreenEvent.OnSortTypeChanged(sortType))
                },
                modifier = Modifier
            )

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
    ToyPlayerTheme {
        MusicAlbumScreen(
            musicAlbumState = MusicAlbumScreenState.default,
            musicPlayerState = MusicPlayerState.default,
            onMusicAlbumEvent = {},
            onMusicPlayerEvent = {},
        )
    }
}