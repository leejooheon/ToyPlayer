package com.jooheon.toyplayer.features.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.audio.VisualizerData
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.common.controller.TouchEventController
import com.jooheon.toyplayer.features.commonui.ext.ObserveAsEvents
import com.jooheon.toyplayer.features.player.component.LogoSection
import com.jooheon.toyplayer.features.player.component.info.InfoSection
import com.jooheon.toyplayer.features.player.component.inside.InsidePager
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import com.jooheon.toyplayer.features.player.model.toChunkedModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visualizer by viewModel.visualizerFlow.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()

    var infoSectionVisibleState by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var screenHideJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(uiState.playlists, uiState.musicState) { // 앱 시작 시 자동 재생하는 부분 - 1: 재생
        if(uiState.playlists.isEmpty()) return@LaunchedEffect
        if(viewModel.autoPlaybackProperty.get()) return@LaunchedEffect
        viewModel.autoPlaybackProperty.set(true)
        if(uiState.musicState.currentPlayingMusic != Song.default) return@LaunchedEffect

        viewModel.dispatch(PlayerEvent.OnPlayAutomatic(context))
    }

    LaunchedEffect(uiState.pagerModel) {
        if(uiState.pagerModel.items.isEmpty()) return@LaunchedEffect
        infoSectionVisibleState = true
    }

    fun restartHideJob() {
        if (!infoSectionVisibleState) return

        Timber.d("restartHideJob")
        screenHideJob?.cancel()
        screenHideJob = null
        screenHideJob = scope.launch(Dispatchers.IO) {
            delay(5.seconds)
            infoSectionVisibleState = false
        }
    }

    ObserveAsEvents(
        flow = TouchEventController.debouncedEvent,
        key1 = infoSectionVisibleState,
    ) {
        restartHideJob()
    }

    LaunchedEffect(infoSectionVisibleState) {
        restartHideJob()
    }

    BackHandler(infoSectionVisibleState) {
        infoSectionVisibleState = false
    }

    PlayerScreenInternal(
        uiState = uiState,
        visualizerData = visualizer,
        currentPosition = currentPosition,
        infoSectionVisibleState = infoSectionVisibleState,
        modifier = Modifier.pointerInput(infoSectionVisibleState) {
            detectTapGestures(
                onTap = { infoSectionVisibleState = !infoSectionVisibleState }
            )
        },
        onPlayerEvent = {
            when(it) {
                is PlayerEvent.OnScreenTouched -> {
                    infoSectionVisibleState = it.state
                    return@PlayerScreenInternal
                }
                is PlayerEvent.OnNavigatePlaylistClick -> {
                    navigateTo.invoke(ScreenNavigation.Playlist.Main)
                }
                is PlayerEvent.OnNavigateSettingClick -> {
                    navigateTo.invoke(ScreenNavigation.Settings.Main)
                }
                is PlayerEvent.OnNavigateLibraryClick -> {
                    navigateTo.invoke(ScreenNavigation.Library)
                }
                is PlayerEvent.OnNavigatePlaylistDetailsClick -> {
                    navigateTo.invoke(ScreenNavigation.Playlist.Details(it.id))
                }
                is PlayerEvent.OnNavigateDlnaClick -> {
                    navigateTo.invoke(ScreenNavigation.Dlna)
                }
                else -> viewModel.dispatch(it)
            }
            restartHideJob()
        },
    )
}

@Composable
private fun PlayerScreenInternal(
    uiState: PlayerUiState,
    visualizerData: VisualizerData,
    currentPosition: Long,
    infoSectionVisibleState: Boolean,
    onPlayerEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val chunkedModel = uiState.playlists.toChunkedModel()
    val channelPagerState = rememberPagerState(
        initialPage = uiState.pagerModel.currentPageIndex(uiState.musicState.currentPlayingMusic.audioId),
        pageCount = { uiState.pagerModel.items.size }
    )

    val infoPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { chunkedModel.size + 1 },
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        InsidePager(
            pagerState = channelPagerState,
            model = uiState.pagerModel,
            currentSong = uiState.musicState.currentPlayingMusic,
            onSwipe = { onPlayerEvent.invoke(PlayerEvent.OnSwipe(it)) },
            modifier = Modifier,
        )

        LogoSection(
            musicState = uiState.musicState,
            visualizerData = visualizerData,
            modifier = Modifier,
        )

        InfoSection(
            pagerState = infoPagerState,
            musicState = uiState.musicState,
            currentPosition = currentPosition,
            playedName = uiState.pagerModel.playedName,
            playedThumbnailImage = uiState.pagerModel.playedThumbnailImage,
            currentSong = uiState.musicState.currentPlayingMusic,
            playlists = uiState.playlists,
            isLoading = uiState.isLoading(),
            isShow = infoSectionVisibleState,
            onCastClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigateDlnaClick) },
            onLibraryClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigateLibraryClick) },
            onPlaylistClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigatePlaylistClick) },
            onSettingClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigateSettingClick) },
            onPlayPauseClick = { onPlayerEvent.invoke(PlayerEvent.OnPlayPauseClick) },
            onNextClick = { onPlayerEvent.invoke(PlayerEvent.OnNextClick) },
            onPreviousClick = { onPlayerEvent.invoke(PlayerEvent.OnPreviousClick) },
            onContentClick = { playlist, index ->
                onPlayerEvent.invoke(PlayerEvent.OnPlaylistClick(playlist, index))
            },
            onFavoriteClick = { playlistId, index ->
                onPlayerEvent.invoke(PlayerEvent.OnFavoriteClick(playlistId, index))
            },
            onDetailsClick = {
                onPlayerEvent.invoke(PlayerEvent.OnNavigatePlaylistDetailsClick(it))
            },
            onSeek = { onPlayerEvent.invoke(PlayerEvent.OnSeek(it)) }
        )
    }
}

@Preview
@Composable
private fun PreviewPlayerScreen() {
    ToyPlayerTheme {
        PlayerScreenInternal(
            uiState = PlayerUiState.preview,
            visualizerData = VisualizerData.default,
            currentPosition = 5000L,
            infoSectionVisibleState = true,
            onPlayerEvent = {},
            modifier = Modifier,
        )
    }
}