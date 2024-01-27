package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.extensions.scrollEnabled
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.MusicSongOptionDialog
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.MusicSongMediaColumn
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components.MusicSongMediaHeader
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.math.max

@Composable
fun MusicSongScreen(
    navController: NavController,
    viewModel: MusicSongScreenViewModel = hiltViewModel(),
) {
    viewModel.navigateToPlayingQueueScreen.observeWithLifecycle { // FIXME: 공통처리 할수있는 방법을 찾아보자
        navController.navigate(ScreenNavigation.Music.PlayingQueue.route)
    }
    val screenState by viewModel.musicPlayerScreenState.collectAsStateWithLifecycle()
    val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

    MusicSongScreen(
        musicSongState = screenState,
        onMusicSongEvent = viewModel::dispatch,
        onMusicMediaItemEvent = viewModel::onSongItemEvent,

        musicPlayerState = musicPlayerState,
        onMusicPlayerEvent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MusicSongScreen(
    musicSongState: MusicSongScreenState,
    onMusicSongEvent: (MusicSongScreenEvent) -> Unit,
    onMusicMediaItemEvent: (SongItemEvent) -> Unit,

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
                onViewTypeClick = { viewType = it} ,
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
            )

            MusicSongMediaColumn(
                musicSongScreenState = musicSongState,
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
                onMediaItemEvent = onMusicMediaItemEvent,
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .scrollEnabled(motionProgress == 0f),
            )
        }
    )

    MusicSongOptionDialog(
        musicListType = musicSongState.musicListType,
        openDialog = openDialog,
        onDismiss = { openDialog = false },
        onOkButtonClicked = {
            openDialog = false
            onMusicSongEvent(MusicSongScreenEvent.OnMusicListTypeChanged(it))
        }
    )
}

@Preview
@Composable
private fun MusicScreenPreview() {
    PreviewTheme(false) {
        MusicSongScreen(
            musicSongState = MusicSongScreenState.default,
            onMusicSongEvent = { _, -> },
            onMusicMediaItemEvent = { },

            musicPlayerState = MusicPlayerState.default.copy(
                musicState = MusicState(
                    currentPlayingMusic = Song.default.copy(
                        title = Resource.longStringPlaceholder,
                        artist = Resource.mediumStringPlaceholder,
                    ),
                )
            ),
            onMusicPlayerEvent = {},
        )
    }
}