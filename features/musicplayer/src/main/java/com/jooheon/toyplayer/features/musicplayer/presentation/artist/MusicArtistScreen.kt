package com.jooheon.toyplayer.features.musicplayer.presentation.artist

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
import androidx.navigation.NavController
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.components.ArtistMediaColumn
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.components.ArtistMediaHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.model.MusicArtistScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.combine
import java.lang.Float
import kotlin.math.max

@Composable
fun MusicArtistScreen(
    navController: NavController,
    viewModel: MusicArtistScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.navigateTo.observeWithLifecycle {
        navController.navigate(it)
    }
    viewModel.sortType.observeWithLifecycle {
        viewModel.loadData(context, it)
    }

    val screenState by viewModel.musicArtistScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicArtistScreen(
        musicArtistState = screenState,
        onMusicArtistScreenEvent = viewModel::dispatch,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MusicArtistScreen(
    musicArtistState: MusicArtistScreenState,
    onMusicArtistScreenEvent: (MusicArtistScreenEvent) -> Unit,

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
            ArtistMediaHeader(
                onDropDownMenuClick = {
                    val type = MusicArtistScreenViewModel.ArtistSortType.entries[it]
                    onMusicArtistScreenEvent(MusicArtistScreenEvent.OnSortTypeChanged(type))
                },
                modifier = Modifier,
            )

            ArtistMediaColumn(
                artists = musicArtistState.artists,
                listState = rememberLazyGridState(),
                onItemClick = { onMusicArtistScreenEvent(MusicArtistScreenEvent.OnArtistItemClick(it)) }
            )
        }
    )
}



@Preview
@Composable
private fun MusicArtistScreenPreview() {
    ToyPlayerTheme {
        MusicArtistScreen(
            musicArtistState = MusicArtistScreenState.default,
            musicPlayerState = MusicPlayerState.default,
            onMusicArtistScreenEvent = {},
            onMusicPlayerEvent = {},
        )
    }
}