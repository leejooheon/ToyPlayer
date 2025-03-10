package com.jooheon.toyplayer.features.player.component.info.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.common.contentHeight
import com.jooheon.toyplayer.features.player.common.horizontalMargin
import com.jooheon.toyplayer.features.player.common.verticalMargin
import com.jooheon.toyplayer.features.player.component.info.control.component.ControlBottomInfo
import com.jooheon.toyplayer.features.player.component.info.control.component.ControlButton
import com.jooheon.toyplayer.features.player.component.info.control.component.ControlTopInfo
import com.jooheon.toyplayer.features.player.model.PlayerUiState


@Composable
internal fun ControlSection(
    musicState: MusicState,
    playlist: Playlist,
    titleAlpha: Float,
    isLoading: Boolean,
    onSettingClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = verticalMargin(),
                start = horizontalMargin(),
                end = horizontalMargin(),
            ),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ControlTopInfo(
                title = playlist.name,
                imageUrl = playlist.thumbnailUrl,
                isPlaying = musicState.isPlaying(),
                onSettingClick = onSettingClick,
                modifier = Modifier.fillMaxWidth(),
            )

            ControlBottomInfo(
                title = musicState.currentPlayingMusic.title,
                artist = musicState.currentPlayingMusic.artist,
                modifier = Modifier.alpha(titleAlpha)
            )
        }

        ControlButton(
            isLoading = isLoading,
            isPlaying = musicState.isPlaying(),
            onClick = onPlayPauseClick,
            modifier = Modifier
                .align(Alignment.Center)
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
            playlist = Playlist.preview.copy(
                name = UiText.StringResource(Strings.placeholder_long).asString()
            ),
            titleAlpha = 1f,
            isLoading = false,
            onSettingClick = {},
            onPlayPauseClick = {},
        )
    }
}