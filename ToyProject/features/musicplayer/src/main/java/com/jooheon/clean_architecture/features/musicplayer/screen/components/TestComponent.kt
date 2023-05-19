package com.jooheon.clean_architecture.features.musicplayer.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import java.lang.Float
import kotlin.math.max
import kotlin.math.min
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.data.albumArtUri
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMotionApi::class)
@Composable
fun TestComponent(
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val musicState = MusicState(
        playlist = listOf(Song.default.copy(
            artist = "artistName",
            title = "Title!!"
        ))
    )

    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    var selectedSongState by remember {
        mutableStateOf<Song?>(null)
    }

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, -swipeAreaHeight to 1)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    LaunchedEffect(key1 = selectedSongState) {
        if (selectedSongState != null && swipeableState.currentValue == 0) {
            swipeableState.animateTo(1)
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(key1 = selectedSongState) {
        musicState.playlist.indexOf(selectedSongState).let { index ->
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
                        if (selectedSongState != it) {
                            selectedSongState = it
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
                            && selectedSongState != null
                        ) 60.dp else 0.dp
                    )
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .layoutId("videoColumn")
            )

            CoilImage(
                url = selectedSongState?.albumArtUri.toString().defaultEmpty(),
                contentDescription = selectedSongState?.title.defaultEmpty(),
                modifier = Modifier
                    .clickable {}
                    .alpha(if (selectedSongState == null) 0f else 1f)
                    .zIndex(if (selectedSongState == null) -1f else 1f)
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Vertical,
                        enabled = selectedSongState != null,
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
                    .alpha(if (selectedSongState == null) 0f else 1f)
                    .zIndex(if (selectedSongState == null) -1f else 1f)
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
                        text = selectedSongState?.title.defaultEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Text(
                        text = selectedSongState?.artist.defaultEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }

                PlayPauseButton(
                    song = selectedSongState ?: Song.default,
                    isPlaying = musicState.isPlaying,
                    onPlayPauseButtonPressed = { selectedSongState = null },
                    modifier = Modifier
                        .alpha(1f - Float.min(motionProgress * 2, 1f))
                )
                PlayListButton(
                    onPlayListButtonPressed = { selectedSongState = null },
                    modifier = Modifier
                        .alpha(1f - Float.min(motionProgress * 2, 1f))
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .zIndex(if (selectedSongState == null) -1f else 1f)
                    .layoutId("content")
            ) {
//                MediaFullPlayer(musicState)

                Spacer(modifier = Modifier.height(16.dp))
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
            }
        }
    }
}


@Preview
@Composable
private fun TestComponentPreview() {
    PreviewTheme(false) {
        TestComponent()
    }
}