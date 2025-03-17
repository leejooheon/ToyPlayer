package com.jooheon.toyplayer.features.player.component.info.content.component

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.common.cardBottomPreviewHeight
import com.jooheon.toyplayer.features.player.common.cardTopPreviewHeight
import com.jooheon.toyplayer.features.player.common.contentSpace
import com.jooheon.toyplayer.features.player.model.PlayerUiState


@Composable
internal fun ContentPagerItem(
    models: List<PlayerUiState.ContentModel>,
    currentSong: Song,
    titleAlpha: Float,
    isPlaying: Boolean,
    onContentClick: (playlistId: Int, startIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier.height(cardBottomPreviewHeight())
        )
        Spacer(
            modifier = Modifier.height(contentSpace())
        )

        models.forEach { model ->
            ContentItem(
                state = state,
                model = model,
                currentSong = currentSong,
                titleAlpha = titleAlpha,
                isPlaying = isPlaying,
                onContentClick = onContentClick,
            )

            Spacer(
                modifier = Modifier.height(contentSpace())
            )
        }

        Spacer(
            modifier = Modifier.height(cardTopPreviewHeight())
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = Color.GRAY.toLong(),
)
@Composable
private fun PreviewContentPagerItem() {
    val uiState = PlayerUiState.preview
    val song = uiState.musicState.currentPlayingMusic
    ToyPlayerTheme {
        ContentPagerItem(
            models = uiState.contentModels,
            currentSong = song,
            titleAlpha = 1f,
            isPlaying = false,
            onContentClick = { _, _ -> },
        )
    }
}