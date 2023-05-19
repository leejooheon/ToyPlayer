package com.jooheon.clean_architecture.features.musicplayer.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.layoutId
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.data.albumArtUri
import com.jooheon.clean_architecture.features.common.R
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme

@Composable
internal fun MediaFullPlayer(
    musicState: MusicState,
// viewModel: MusicPlayerViewModel
) {
//    val musicState by viewModel.musicState.collectAsState()
//    val timePassed by viewModel.timePassed.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CoilImage(
            url = musicState.currentPlayingMusic.albumArtUri.toString(),
            contentDescription = musicState.currentPlayingMusic.title,
            shape = RoundedCornerShape(10.dp),
            placeholderRes = R.drawable.ic_placeholder,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1.0f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .layoutId("column_title_artist")
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = musicState.currentPlayingMusic.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = musicState.currentPlayingMusic.artist,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.layoutId("column_music_control")
        ) {
            MusicProgress(
                modifier = Modifier.fillMaxWidth(0.8f),
                maxDuration = musicState.currentPlayingMusic.duration,
                currentDuration = 1L, // FIXME
                onChanged = { progress ->
                    val duration = progress * musicState.currentPlayingMusic.duration
//                    viewModel.snapTo(duration.toLong())
                }
            )

            MusicControlButtons(
                isPlaying = musicState.isPlaying,
                onNext = {}, // viewModel::onNext,
                onPrevious = {}, //viewModel::onPrevious,
                onPlayPauseButtonPressed = {
                    val song = musicState.currentPlayingMusic
//                    viewModel.onPlayPauseButtonPressed(song)
                }
            )

            OtherButtons(
                repeatMode = musicState.repeatMode,
                shuffleMode = musicState.shuffleMode,
                onShuffleModePressed = { }, //viewModel::onShuffleButtonPressed,
                onRepeatModePressed = { }, // viewModel::onRepeatButtonPressed,
            )
        }
    }
}


@Preview
@Composable
private fun TestComponentPreview() {
    PreviewTheme(false) {
        MediaFullPlayer(
            musicState = MusicState()
        )
    }
}