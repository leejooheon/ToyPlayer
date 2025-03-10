package com.jooheon.toyplayer.features.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.player.component.info.InfoSection
import com.jooheon.toyplayer.features.player.component.inside.InsidePager
import com.jooheon.toyplayer.features.player.model.PlayerAnimateState
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import com.jooheon.toyplayer.features.player.model.toChunkedModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerScreen(
    onBackPressed: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val contentState by viewModel.savedContentState.collectAsStateWithLifecycle()

    var infoSectionVisibleState by remember { mutableStateOf(false) }
    var autoPlaybackStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadData(context)
    }

    LaunchedEffect(uiState.contentModels) { // 앱 시작 시 자동 재생하는 부분 - 1: 재생
        if(uiState.contentModels.isEmpty()) return@LaunchedEffect
        if(viewModel.autoPlaybackProperty.get()) return@LaunchedEffect

        val firstPlaylist = uiState.contentModels.first().playlist
        val event = PlayerEvent.OnPlayAutomatic(firstPlaylist)

        viewModel.autoPlaybackProperty.set(true)
        viewModel.dispatch(event)
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
            else -> onBackPressed.invoke()
        }
    }

    PlayerScreen(
        uiState = uiState,
        contentState = contentState,
        infoSectionVisibleState = infoSectionVisibleState,
        onPlayerEvent = {
            if(it is PlayerEvent.OnScreenTouched) infoSectionVisibleState = it.state
            else viewModel.dispatch(it)
        },
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun PlayerScreen(
    uiState: PlayerUiState,
    contentState: Long,
    infoSectionVisibleState: Boolean,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    var animateState by remember { mutableStateOf(PlayerAnimateState.default) }
    val loadingState by rememberUpdatedState(uiState.isLoading())

    var showSwipeGuideState by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    var screenHideJob by remember { mutableStateOf<Job?>(null) }
    val touchEventState = remember { MutableSharedFlow<Float>() }

    val channelPagerState = rememberPagerState(
        initialPage = uiState.pagerModel.currentPageIndex(uiState.musicState.currentPlayingMusic.key()),
        pageCount = { uiState.pagerModel.items.size }
    )

    val infoPagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uiState.contentModels.toChunkedModel().size + 1 },
    )

    LaunchedEffect(contentState) {
        if(contentState == 0L) return@LaunchedEffect
        animateState = PlayerAnimateState.default.copy(start = true)
    }

    LaunchedEffect(infoSectionVisibleState) {
        fun restartHideJob() {
            screenHideJob?.cancel()
            screenHideJob = null
            screenHideJob = scope.launch(Dispatchers.IO) {
                delay(5.seconds)
                onPlayerEvent.invoke(PlayerEvent.OnScreenTouched(false))
            }
        }
        launch {
            Timber.d("infoSectionVisibleState: $infoSectionVisibleState")
            if (!infoSectionVisibleState) return@launch
            restartHideJob()
        }
        launch {
            touchEventState.debounce(500).collectLatest {
                Timber.d("restartHideJob")
                restartHideJob()
            }
        }
    }
    LaunchedEffect(infoSectionVisibleState, animateState, loadingState) {
        if(infoSectionVisibleState) {
            animateState.cancel()
            return@LaunchedEffect
        }

        if(animateState.shouldRun) {
            while(loadingState) {
                Timber.d("startAnimationDelay: await playback")
                withContext(Dispatchers.IO) { delay(1.seconds) }
            }
            try { animateState.start(showSwipeGuideState) }
            finally {
                showSwipeGuideState = false
                animateState.cancel()
            }
        }
    }

    LaunchedEffect(channelPagerState, animateState.swipeAnimState.value) {
        val (state, time) = animateState.swipeAnimState.value
        channelPagerState.animateScrollToPage(
            page = channelPagerState.currentPage,
            pageOffsetFraction = if(state) 0.1f else 0f,
            animationSpec = tween(durationMillis = time)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(infoSectionVisibleState) {
                detectTapGestures(
                    onTap = {
                        onPlayerEvent.invoke(PlayerEvent.OnScreenTouched(!infoSectionVisibleState))
                    }
                )
            }
    ) {
        InsidePager(
            pagerState = channelPagerState,
            model = uiState.pagerModel,
            currentSong = uiState.musicState.currentPlayingMusic,
            onSwipe = {
                onPlayerEvent.invoke(PlayerEvent.OnSwipe(it))
            },
//            onImageReady = {
//                animateState = animateState.copy(imageReady = true)
//            },
            modifier = Modifier.graphicsLayer(alpha = animateState.containerAnimation.value),
        )

        InfoSection(
            pagerState = infoPagerState,
            musicState = uiState.musicState,
            currentPlaylist = uiState.pagerModel.currentPlaylist,
            currentSong = uiState.musicState.currentPlayingMusic,
            models = uiState.contentModels,
            isLoading = uiState.isLoading(),
            isShow = infoSectionVisibleState,
            onOffsetChanged = {
                scope.launch { touchEventState.emit(it) }
            },
            onSettingClick = {
                onPlayerEvent.invoke(PlayerEvent.OnSettingClick)
            },
            onPlayPauseClick = {
                onPlayerEvent.invoke(PlayerEvent.OnPlayPauseClick)
            },
            onContentClick = { playlistId, song ->
                animateState = PlayerAnimateState.default
                onPlayerEvent.invoke(PlayerEvent.OnContentClick(playlistId, song))
            },
        )
    }
}

@Preview
@Composable
private fun PreviewLgPlayerScreen() {
    ToyPlayerTheme {
        PlayerScreen(
            uiState = PlayerUiState.preview,
            contentState = 0L,
            infoSectionVisibleState = true,
            onPlayerEvent = {},
//            onPlayerCommonEvent = {},
        )
    }
}