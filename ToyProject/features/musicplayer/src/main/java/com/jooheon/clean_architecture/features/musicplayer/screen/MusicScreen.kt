package com.jooheon.clean_architecture.features.musicplayer.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.screen.components.MediaColumn
import com.jooheon.clean_architecture.features.musicplayer.screen.components.MusicControlButtons
import com.jooheon.clean_architecture.features.musicplayer.screen.components.MusicProgress
import com.jooheon.clean_architecture.features.musicplayer.screen.components.OtherButtons
import com.jooheon.clean_architecture.features.musicplayer.screen.components.PlayListButton
import com.jooheon.clean_architecture.features.musicplayer.screen.components.PlayPauseButton
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.data.albumArtUri
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterialApi::class, ExperimentalMotionApi::class)
@Composable
fun MusicScreen(
    navigator: NavController,
    viewModel: MusicScreenViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val musicState by viewModel.musicState.collectAsState()
    val duration by viewModel.duration.collectAsState()

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
                onItemClick = {
                    if (swipeableState.currentValue == 0) {
                        if (musicState.currentPlayingMusic != it) {
                            viewModel.onPlay(it)
                        } else {
                            scope.launch {
                                swipeableState.animateTo(1)
                            }
                        }
                    }
                },
                playlist = musicState.playlist,
                listState = listState,
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
                    .layoutId("videoColumn")
            )

            CoilImage(
                url = musicState.currentPlayingMusic.albumArtUri.toString().defaultEmpty(),
                contentDescription = musicState.currentPlayingMusic.title.defaultEmpty(),
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
                    .layoutId("thumbnail"),
            )
//            AlbumImage(
//                isPlaying = musicState.isPlaying,
//                song = selectedSongState ?: Song.default,
//                modifier = Modifier
//                    .clickable {}
//                    .alpha(if (selectedSongState == null) 0f else 1f)
//                    .zIndex(if (selectedSongState == null) -1f else 1f)
//                    .swipeable(
//                        state = swipeableState,
//                        anchors = anchors,
//                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
//                        orientation = Orientation.Vertical,
//                        enabled = selectedSongState != null,
//                    )
//                    .layoutId("thumbnail")
//            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
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
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(1f - min(motionProgress * 2, 1f))
                ) {
                    Text(
                        text = musicState.currentPlayingMusic.title.defaultEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Text(
                        text = musicState.currentPlayingMusic.artist.defaultEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }

                PlayPauseButton(
                    song = musicState.currentPlayingMusic,
                    isPlaying = musicState.isPlaying,
                    onPlayPauseButtonPressed = {
                        viewModel.onPlayPauseButtonClicked(musicState.currentPlayingMusic)
                    },
                    modifier = Modifier
                        .alpha(1f - Float.min(motionProgress * 2, 1f))
                )
                PlayListButton(
                    onPlayListButtonPressed = { viewModel.onPlay(Song.default) },
                    modifier = Modifier
                        .alpha(1f - Float.min(motionProgress * 2, 1f))
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .zIndex(if (musicState.currentPlayingMusic == Song.default) -1f else 1f)
                    .layoutId("content"),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = musicState.currentPlayingMusic.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = musicState.currentPlayingMusic.artist,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MusicProgress(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        maxDuration = musicState.currentPlayingMusic.duration,
                        currentDuration = duration,
                        onChanged = {
                            val progress = it * musicState.currentPlayingMusic.duration
                            viewModel.snapTo(progress.toLong())
                        }
                    )

                    MusicControlButtons(
                        isPlaying = musicState.isPlaying,
                        onNext = viewModel::onNextClicked,
                        onPrevious = viewModel::onPreviousClicked,
                        onPlayPauseButtonPressed = {
                            val song = musicState.currentPlayingMusic
                            viewModel.onPlayPauseButtonClicked(song)
                        }
                    )

                    OtherButtons(
                        repeatMode = musicState.repeatMode,
                        shuffleMode = musicState.shuffleMode,
                        onShuffleModePressed = viewModel::onShuffleClicked,
                        onRepeatModePressed = viewModel::onRepeatClicked,
                    )
                }
            }
        }
    }
}
@Preview
@Composable
private fun MusicScreenPreview() {
    val context = LocalContext.current
    PreviewTheme(false) {
        MusicScreen(
            navigator = NavController(context),
        )
    }
}