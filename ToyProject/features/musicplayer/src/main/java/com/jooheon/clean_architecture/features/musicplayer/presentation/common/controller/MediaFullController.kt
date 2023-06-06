package com.jooheon.clean_architecture.features.musicplayer.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MediaFullController(
    musicPlayerScreenState: MusicPlayerScreenState,
    onPlayPauseButtonClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onRepeatClicked: () -> Unit,
    snapTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val musicState = musicPlayerScreenState.musicState
    val duration = musicPlayerScreenState.currentDuration

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        MusicProgress(
            modifier = Modifier.fillMaxWidth(0.9f),
            maxDuration = musicState.currentPlayingMusic.duration,
            currentDuration = duration,
            onChanged = {
                val progress = it * musicState.currentPlayingMusic.duration
                snapTo(progress.toLong())
            }
        )

        MusicControlButtons(
            isPlaying = musicState.isPlaying,
            repeatMode = musicState.repeatMode,
            shuffleMode = musicState.shuffleMode,
            onRepeatModePressed = onRepeatClicked,
            onPrevious = onPreviousClicked,
            onPlayPauseButtonPressed = onPlayPauseButtonClicked,
            onNext = onNextClicked,
            onShuffleModePressed = onShuffleClicked,
            modifier = Modifier
                .fillMaxWidth(0.95f)
        )
    }
}

@Preview
@Composable
private fun MediaFullControllerPreviewDark() {
    val musicStateFlow = MutableStateFlow(MusicState())
    val durationStateFlow = MutableStateFlow(320000L)
    PreviewTheme(true) {
        Surface(modifier = Modifier.width(400.dp)) {

            MediaFullController(
                musicPlayerScreenState = MusicPlayerScreenState.default,
                onPlayPauseButtonClicked = { },
                onNextClicked = {},
                onPreviousClicked = {},
                onShuffleClicked = {},
                onRepeatClicked = {},
                snapTo = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Preview
@Composable
private fun MediaFullControllerPreviewLight() {
    PreviewTheme(false) {
        Surface(modifier = Modifier.width(400.dp)) {

            MediaFullController(
                musicPlayerScreenState = MusicPlayerScreenState.default,
                onPlayPauseButtonClicked = { },
                onNextClicked = {},
                onPreviousClicked = {},
                onShuffleClicked = {},
                onRepeatClicked = {},
                snapTo = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}