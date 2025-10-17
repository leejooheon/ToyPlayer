package com.jooheon.toyplayer.features.player.component.info.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.common.contentHeight
import com.jooheon.toyplayer.features.player.common.horizontalMargin
import com.jooheon.toyplayer.features.player.component.info.control.component.ControlBottomInfo
import com.jooheon.toyplayer.features.player.component.info.control.component.ControlButton
import com.jooheon.toyplayer.features.player.component.info.control.component.ControlTopInfo
import com.jooheon.toyplayer.features.player.model.PlayerUiState


@Composable
internal fun ControlSection(
    musicState: MusicState,
    currentPosition: Long,
    playedName: String,
    playedThumbnailImage: String,
    titleAlpha: Float,
    isLoading: Boolean,
    onCastClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onSettingClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ControlTopInfo(
                title = playedName,
                imageUrl = playedThumbnailImage,
                isPlaying = musicState.isPlaying(),
                onCastClick = onCastClick,
                onLibraryClick = onLibraryClick,
                onPlaylistClick = onPlaylistClick,
                onSettingClick = onSettingClick,
                modifier = Modifier.fillMaxWidth(),
            )

            ControlBottomInfo(
                title = musicState.currentPlayingMusic.title,
                artist = musicState.currentPlayingMusic.artist,
                duration = musicState.currentPlayingMusic.duration,
                currentPosition = currentPosition,
                onSeek = onSeek,
                modifier = Modifier.alpha(titleAlpha)
            )
        }

        ControlButton(
            isLoading = isLoading,
            isPlaying = musicState.isPlaying(),
            onPlayPauseClick = onPlayPauseClick,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(bottom = contentHeight() * 0.6f),
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewControlSection() {
    val uiState = PlayerUiState.preview
    ToyPlayerTheme {
        ControlSection(
            musicState = uiState.musicState,
            currentPosition = 5000L,
            playedName = UiText.StringResource(Strings.placeholder_long).asString(),
            playedThumbnailImage = "",
            titleAlpha = 1f,
            isLoading = false,
            onCastClick = {},
            onLibraryClick = {},
            onPlaylistClick = {},
            onSettingClick = {},
            onPlayPauseClick = {},
            onNextClick = {},
            onPreviousClick = {},
            onSeek = {},
            modifier = Modifier,
        )
    }
}