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
import com.jooheon.toyplayer.features.common.compose.ObserveAsEvents
import com.jooheon.toyplayer.features.common.compose.TouchEventController
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

    var infoSectionVisibleState by remember { mutableStateOf(false) }
    var autoPlaybackStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(uiState.contentModels) { // 앱 시작 시 자동 재생하는 부분 - 1: 재생
        if(uiState.contentModels.isEmpty()) return@LaunchedEffect
        if(viewModel.autoPlaybackProperty.get()) return@LaunchedEffect

        viewModel.autoPlaybackProperty.set(true)
        viewModel.dispatch(context, PlayerEvent.OnPlayAutomatic)
    }

    LaunchedEffect(autoPlaybackStarted, uiState.pagerModel) { // 앱 시작 시 자동 재생하는 부분 - 2: 정보 화면 표시
        if(!autoPlaybackStarted) return@LaunchedEffect
        if(uiState.pagerModel == PlayerUiState.default.pagerModel) return@LaunchedEffect
        autoPlaybackStarted = false
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
                is PlayerEvent.OnPlaylistClick -> {
                    navigateTo.invoke(ScreenNavigation.Playlist.Main)
                }
                is PlayerEvent.OnSettingClick -> {
                    navigateTo.invoke(ScreenNavigation.Setting.Main)
                }
                is PlayerEvent.OnLibraryClick -> {
                    navigateTo.invoke(ScreenNavigation.Library)
                }
                else -> viewModel.dispatch(context, it)
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
        pageCount = { uiState.contentModels.toChunkedModel().size + 1 },
    )

    ObserveAsEvents(
        flow = TouchEventController.debouncedEvent,
        key1 = infoSectionVisibleState,
    ) {
        if(infoSectionVisibleState) {
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

        InfoSection(
            pagerState = infoPagerState,
            musicState = uiState.musicState,
            currentPlaylist = uiState.pagerModel.currentPlaylist,
            currentSong = uiState.musicState.currentPlayingMusic,
            models = uiState.contentModels,
            isLoading = uiState.isLoading(),
            isShow = infoSectionVisibleState,
            onLibraryClick = { onPlayerEvent.invoke(PlayerEvent.OnLibraryClick) },
            onPlaylistClick = { onPlayerEvent.invoke(PlayerEvent.OnPlaylistClick) },
            onSettingClick = { onPlayerEvent.invoke(PlayerEvent.OnSettingClick) },
            onPlayPauseClick = { onPlayerEvent.invoke(PlayerEvent.OnPlayPauseClick) },
            onNextClick = { onPlayerEvent.invoke(PlayerEvent.OnNextClick) },
            onPreviousClick = { onPlayerEvent.invoke(PlayerEvent.OnPreviousClick) },
            onContentClick = { playlist, index ->
                onPlayerEvent.invoke(PlayerEvent.OnContentClick(playlist, index))
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