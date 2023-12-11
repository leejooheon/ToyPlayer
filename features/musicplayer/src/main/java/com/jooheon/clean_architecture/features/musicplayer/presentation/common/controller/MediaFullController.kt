package com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.ext.isPlaying
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun MediaFullController(
    musicPlayerState: MusicPlayerState,
    onPlayPauseButtonClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onRepeatClicked: () -> Unit,
    snapTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val musicState = musicPlayerState.musicState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        MusicProgress(
            modifier = Modifier.fillMaxWidth(0.9f),
            maxDuration = musicState.currentPlayingMusic.duration,
            currentDuration = musicState.timePassed,
            onChanged = {
                val progress = it * musicState.currentPlayingMusic.duration
                snapTo(progress.toLong())
            }
        )

        MusicControlButtons(
            isPlaying = musicState.playbackState.isPlaying,
            repeatMode = musicPlayerState.repeatMode,
            shuffleMode = musicPlayerState.shuffleMode,
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
    PreviewTheme(true) {
        Surface(modifier = Modifier.width(400.dp)) {

            MediaFullController(
                musicPlayerState = MusicPlayerState.default,
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
                musicPlayerState = MusicPlayerState.default,
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