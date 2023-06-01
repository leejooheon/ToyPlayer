package com.jooheon.clean_architecture.features.musicplayer.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.AlbumImage
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaBottomController
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaColumn
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaFullController
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaFullDetails
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class, ExperimentalMotionApi::class)
@Composable
fun MusicScreen(
    musicPlayerScreenState: MusicPlayerScreenState,
    onEvent: (MusicPlayerScreenEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, -swipeAreaHeight to 1)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    val musicState = musicPlayerScreenState.musicState

    LaunchedEffect(key1 = musicState.currentPlayingMusic) {
        if (musicState.currentPlayingMusic != Song.default && swipeableState.currentValue == 0) {
            swipeableState.animateTo(1)
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(key1 = musicState.currentPlayingMusic) {
        musicState.playlist.indexOf(musicState.currentPlayingMusic).let { index ->
            if (index > -1)
                listState.animateScrollToItem(index)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MotionLayout(
            motionScene = MotionScene(content = motionSceneContent),
            progress = motionProgress,
            modifier = Modifier.fillMaxSize()
        ) {
            MediaColumn(
                playlist = musicState.playlist,
                listState = listState,
                onItemClick = {
                    if (swipeableState.currentValue == 0) {
                        if (musicState.currentPlayingMusic != it) {
                            onEvent(MusicPlayerScreenEvent.OnPlayClick(it))
                        } else {
                            scope.launch {
                                swipeableState.animateTo(1)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(
                        bottom = if (
                            swipeableState.progress.from == 0
                            && swipeableState.progress.to == 0
                            && musicState.currentPlayingMusic != Song.default
                        ) 60.dp else 0.dp
                    )
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .layoutId("mediaColumn"),
            )

            AlbumImage(
                song = musicState.currentPlayingMusic,
                isPlaying = musicState.isPlaying,
                modifier = Modifier
                    .clickable {}
                    .alpha(if (musicState.currentPlayingMusic == Song.default) 0f else 1f)
                    .zIndex(if (musicState.currentPlayingMusic == Song.default) -1f else 1f)
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Vertical,
                        enabled = musicState.currentPlayingMusic != Song.default,
                    )
                    .layoutId("albumImage"),
            )

            MediaBottomController(
                motionProgress = motionProgress,
                song = musicState.currentPlayingMusic,
                isPlaying = musicState.isPlaying,
                onPlayPauseButtonClicked = { onEvent(MusicPlayerScreenEvent.OnPlayPauseClick(it)) },
                onPlayListButtonPressed = { onEvent(MusicPlayerScreenEvent.OnPlayPauseClick(Song.default)) },
                modifier = Modifier
                    .clickable {
                        scope.launch { swipeableState.animateTo(1) }
                    }
                    .alpha(if (musicState.currentPlayingMusic == Song.default) 0f else 1f)
                    .zIndex(if (musicState.currentPlayingMusic == Song.default) -1f else 1f)
                    .fillMaxWidth()
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Vertical,
                        enabled = true
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .padding(all = 10.dp)
                    .layoutId("details")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .zIndex(if (musicState.currentPlayingMusic == Song.default) -1f else 1f)
                    .layoutId("content"),
            ) {
                MediaFullDetails(
                    song = musicState.currentPlayingMusic,
                    modifier = Modifier
                        .alpha(Float.min(motionProgress, 1f))
                        .fillMaxWidth()
                )
                MediaFullController(
                    musicPlayerScreenState = musicPlayerScreenState,
                    onPlayPauseButtonClicked = { onEvent(MusicPlayerScreenEvent.OnPlayPauseClick(musicState.currentPlayingMusic)) },
                    onNextClicked = { onEvent(MusicPlayerScreenEvent.OnNextClick) }, //viewModel::onNextClicked,
                    onPreviousClicked = { onEvent(MusicPlayerScreenEvent.OnPreviousClick) },//viewModel::onPreviousClicked,
                    onShuffleClicked = { onEvent(MusicPlayerScreenEvent.OnShuffleClick)}, //viewModel::onShuffleClicked,
                    onRepeatClicked = { onEvent(MusicPlayerScreenEvent.OnRepeatClick) },
                    snapTo = { onEvent(MusicPlayerScreenEvent.OnSnapTo(it)) } ,
                    modifier = Modifier
                        .alpha(Float.min(motionProgress, 1f))
                        .fillMaxWidth()
                )
            }
        }
    }
}
@Preview
@Composable
private fun MusicScreenPreview() {
    PreviewTheme(false) {
        MusicScreen(
            musicPlayerScreenState = MusicPlayerScreenState.default.copy(
                musicState = MusicState(
                    playlist = listOf(Song.default, Song.default,)
                )
            ),
            onEvent = { _, -> }
        )
    }
}