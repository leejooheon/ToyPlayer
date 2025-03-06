package com.jooheon.toyplayer.features.musicplayer.presentation.song.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.music.MusicListType
import com.jooheon.toyplayer.features.common.compose.extensions.scrollEnabled
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicplayer.presentation.song.components.MusicSongMediaHeader
import com.jooheon.toyplayer.features.musicplayer.presentation.song.components.MusicSongOptionDialog
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.components.MusicListDetailComponent
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.model.MusicListDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.model.MusicListDetailScreenState
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.OptIn
import kotlin.Unit
import kotlin.let
import kotlin.math.max
import kotlin.with

@Composable
fun MusicListDetailScreen(
    onBackClick: () -> Unit,
    navigate: (ScreenNavigation.Music) -> Unit,
    viewModel: MusicListDetailViewModel = hiltViewModel(),
) {
    viewModel.navigateTo.observeWithLifecycle { // FIXME: 공통처리 할수있는 방법을 찾아보자
        (it as? ScreenNavigation.Music)?.let {
            navigate.invoke(it)
        }
    }

    val screenState by viewModel.musicListDetailScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicListDetailScreen(
        musicListDetailScreenState = screenState,
        onMusicListDetailScreenEvent = {},

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MusicListDetailScreen(
    musicListDetailScreenState: MusicListDetailScreenState,
    onMusicListDetailScreenEvent: (MusicListDetailScreenEvent) -> Unit,

    musicPlayerState: MusicPlayerState,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val musicState = musicPlayerState.musicState

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    var openDialog by remember { mutableStateOf(false) }
    var viewType by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    MediaSwipeableLayout(
        musicPlayerState = musicPlayerState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerEvent,
        content = {
            MusicSongMediaHeader(
                viewType = viewType,
                onSeeMoreButtonClick = { openDialog = true },
                onViewTypeClick = { viewType = it},
                onPlayAllClick = {
                    onMusicPlayerEvent(
                        MusicPlayerEvent.OnEnqueue(
                            songs = musicListDetailScreenState.songList,
                            shuffle = true,
                            playWhenReady = true
                        )
                    )
                },
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
            )
            MusicListDetailComponent(
                playlists = musicListDetailScreenState.playlists,
                songList = musicListDetailScreenState.songList,
                songMediaColumnItemType = viewType,
                listState = listState,
                onSongClick = {
                    if (swipeableState.currentValue == 0) {
                        if (musicState.currentPlayingMusic != it) {
                            onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(it))
                        } else {
                            scope.launch {
                                swipeableState.animateTo(1)
                            }
                        }
                    }
                },
                onMediaItemEvent = {},
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .scrollEnabled(motionProgress == 0f),
            )
        }
    )

    MusicSongOptionDialog(
        musicListType = musicListDetailScreenState.musicListType,
        openDialog = openDialog,
        onDismiss = { openDialog = false },
        onOkButtonClicked = {
            openDialog = false
        }
    )
}