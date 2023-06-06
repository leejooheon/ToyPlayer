package com.jooheon.clean_architecture.features.musicplayer.presentation.song

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
import com.jooheon.clean_architecture.features.common.compose.extensions.scrollEnabled
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.components.MusicSongOptionDialog
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.components.MusicSongMediaColumn
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.components.MusicSongMediaHeader
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicSongScreen(
    musicPlayerScreenState: MusicPlayerScreenState,

    onEvent: (MusicPlayerScreenEvent) -> Unit,
    onMusicMediaItemEvent: (MusicMediaItemEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val musicState = musicPlayerScreenState.musicState

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    var openDialog by remember { mutableStateOf(false) }
    var viewType by rememberSaveable { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onEvent,
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
                musicPlayerScreenState = musicPlayerScreenState,
                songMediaColumnItemType = viewType,
                listState = listState,
                onSongClick = {
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
                onMediaItemEvent = onMusicMediaItemEvent,
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .scrollEnabled(motionProgress == 0f),
            )
        }
    )

    MusicSongOptionDialog( // FIXME
        playlistType = musicPlayerScreenState.musicState.playlistType,
        openDialog = openDialog,
        onDismiss = { openDialog = false },
        onOkButtonClicked = {
            openDialog = false
            onEvent(MusicPlayerScreenEvent.OnPlaylistTypeChanged(it))
        }
    )
}

@Preview
@Composable
private fun MusicScreenPreview() {
    PreviewTheme(false) {
        MusicSongScreen(
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onEvent = { _, -> },
            onMusicMediaItemEvent = { }
        )
    }
}