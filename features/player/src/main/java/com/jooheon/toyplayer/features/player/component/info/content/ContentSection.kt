package com.jooheon.toyplayer.features.player.component.info.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.component.info.content.component.ContentPagerItem
import com.jooheon.toyplayer.features.player.component.info.content.component.ContentScrollableItem
import com.jooheon.toyplayer.features.player.model.PlayerUiState

@Composable
internal fun ContentSection(
    useScrollableItem: Boolean,
    models: List<PlayerUiState.ContentModel>,
    currentSong: Song,
    titleAlpha: Float,
    contentAlpha: Float,
    isPlaying: Boolean,
    enableScroll: Boolean,
    onContentClick: (playlistId: Int, startIndex: Int) -> Unit,
    onContentAlphaChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (useScrollableItem) {
        ContentScrollableItem(
            models = models,
            currentSong = currentSong,
            titleAlpha = titleAlpha,
            isPlaying = isPlaying,
            enableScroll = enableScroll,
            onContentClick = onContentClick,
            onContentAlphaChanged = onContentAlphaChanged,
            modifier = modifier,
        )
    } else {
        ContentPagerItem(
            models = models,
            currentSong = currentSong,
            titleAlpha = titleAlpha,
            isPlaying = isPlaying,
            onContentClick = onContentClick,
            modifier = modifier.alpha(contentAlpha),
        )
    }
}