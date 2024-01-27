package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.components.ArtistDetailMediaColumn
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model.MusicArtistDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import java.lang.Float
import kotlin.math.max

@Composable
fun MusicArtistDetailScreen(
    navController: NavController,
    artist: Artist,
    viewModel: MusicArtistDetailScreenViewModel = hiltViewModel()
) {
    viewModel.initialize(artist)
    viewModel.navigateTo.observeWithLifecycle { route ->
        if(route == ScreenNavigation.Back.route) {
            navController.popBackStack()
        } else {
            navController.navigate(route)
        }
    }
    viewModel.navigateToPlayingQueueScreen.observeWithLifecycle {
        navController.navigate(ScreenNavigation.Music.PlayingQueue.route)
    }
    val state by viewModel.musicArtistDetailScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicArtistDetailScreen(
        musicArtistDetailScreenState = state,
        onMusicArtistDetailScreenEvent = viewModel::dispatch,
        onMusicMediaItemEvent = viewModel::onSongItemEvent,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MusicArtistDetailScreen(
    musicArtistDetailScreenState: MusicArtistDetailScreenState,
    onMusicArtistDetailScreenEvent: (MusicArtistDetailScreenEvent) -> Unit,
    onMusicMediaItemEvent: (SongItemEvent) -> Unit,

    musicPlayerState: MusicPlayerState,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val listState = rememberLazyListState()

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SmallTopAppBar(
            title = {
                Text(
                    text = musicArtistDetailScreenState.artist.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { onMusicArtistDetailScreenEvent(MusicArtistDetailScreenEvent.OnBackClick) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
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
                ArtistDetailMediaColumn(
                    musicArtistDetailScreenState = musicArtistDetailScreenState,
                    listState = listState,
                    onEvent = onMusicArtistDetailScreenEvent,
                    onMediaItemEvent = onMusicMediaItemEvent,
                    onMusicPlayerEvent = onMusicPlayerEvent,
                )
            }
        )
    }

    BackHandler {
        onMusicArtistDetailScreenEvent(MusicArtistDetailScreenEvent.OnBackClick)
    }
}

@Preview
@Composable
private fun MusicArtistDetailScreenPreview() {
    PreviewTheme(false) {
        MusicArtistDetailScreen(
            musicArtistDetailScreenState = MusicArtistDetailScreenState.default,
            onMusicArtistDetailScreenEvent = {},
            onMusicMediaItemEvent = {},

            musicPlayerState = MusicPlayerState.default,
            onMusicPlayerEvent = {},
        )
    }
}