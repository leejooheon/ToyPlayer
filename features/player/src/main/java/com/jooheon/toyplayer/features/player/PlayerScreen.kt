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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.commonui.ext.ObserveAsEvents
import com.jooheon.toyplayer.features.commonui.controller.TouchEventController
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var infoSectionVisibleState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(uiState.playlists) { // 앱 시작 시 자동 재생하는 부분 - 1: 재생
        if(uiState.playlists.isEmpty()) return@LaunchedEffect
        if(viewModel.autoPlaybackProperty.get()) return@LaunchedEffect
        viewModel.autoPlaybackProperty.set(true)
        if(uiState.musicState.isPlaying()) return@LaunchedEffect

        viewModel.dispatch(PlayerEvent.OnPlayAutomatic)
        infoSectionVisibleState = true
    }

    BackHandler {
        when {
            infoSectionVisibleState -> {
                infoSectionVisibleState = false
                return@BackHandler
            }
            else -> {
                navigateTo.invoke(ScreenNavigation.Back)
                return@BackHandler
            }
        }
    }

    PlayerScreenInternal(
        uiState = uiState,
        infoSectionVisibleState = infoSectionVisibleState,
        onPlayerEvent = {
            when(it) {
                is PlayerEvent.OnScreenTouched -> {
                    infoSectionVisibleState = it.state
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
                else -> viewModel.dispatch(it)
            }
        },
    )
}

@Composable
private fun PlayerScreenInternal(
    uiState: PlayerUiState,
    infoSectionVisibleState: Boolean,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var screenHideJob by remember { mutableStateOf<Job?>(null) }

    fun restartHideJob() {
        Timber.d("restartHideJob")
        screenHideJob?.cancel()
        screenHideJob = null
        screenHideJob = scope.launch(Dispatchers.IO) {
            delay(5.seconds)
            onPlayerEvent.invoke(PlayerEvent.OnScreenTouched(false))
        }
    }

    val channelPagerState = rememberPagerState(
        initialPage = uiState.pagerModel.currentPageIndex(uiState.musicState.currentPlayingMusic.key()),
        pageCount = { uiState.pagerModel.items.size }
    )

    val infoPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uiState.playlists.toChunkedModel().size + 1 },
    )

    ObserveAsEvents(
        flow = TouchEventController.debouncedEvent,
        key1 = infoSectionVisibleState,
    ) {
        if (infoSectionVisibleState) {
            restartHideJob()
        }
    }

    LaunchedEffect(infoSectionVisibleState) {
        Timber.d("infoSectionVisibleState: $infoSectionVisibleState")
        if (infoSectionVisibleState) {
            restartHideJob()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(infoSectionVisibleState) {
                detectTapGestures(
                    onTap = {
                        val event = PlayerEvent.OnScreenTouched(!infoSectionVisibleState)
                        onPlayerEvent.invoke(event)
                    }
                )
            }
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
            onFavoriteClick = { onPlayerEvent.invoke(PlayerEvent.OnFavoriteClick(it)) },
            modifier = Modifier,
        )

        InfoSection(
            pagerState = infoPagerState,
            musicState = uiState.musicState,
            playedName = uiState.pagerModel.playedName,
            playedThumbnailImage = uiState.pagerModel.playedThumbnailImage,
            currentSong = uiState.musicState.currentPlayingMusic,
            playlists = uiState.playlists,
            isLoading = uiState.isLoading(),
            isShow = infoSectionVisibleState,
            onLibraryClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigateLibraryClick) },
            onPlaylistClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigatePlaylistClick) },
            onSettingClick = { onPlayerEvent.invoke(PlayerEvent.OnNavigateSettingClick) },
            onPlayPauseClick = { onPlayerEvent.invoke(PlayerEvent.OnPlayPauseClick) },
            onNextClick = { onPlayerEvent.invoke(PlayerEvent.OnNextClick) },
            onPreviousClick = { onPlayerEvent.invoke(PlayerEvent.OnPreviousClick) },
            onContentClick = { playlist, index ->
                onPlayerEvent.invoke(PlayerEvent.OnPlaylistClick(playlist, index))
            },
        )
    }
}

@Preview
@Composable
private fun PreviewLgPlayerScreen() {
    ToyPlayerTheme {
        PlayerScreenInternal(
            uiState = PlayerUiState.preview,
            infoSectionVisibleState = true,
            onPlayerEvent = {},
        )
    }
}