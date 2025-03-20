package com.jooheon.toyplayer.features.player.component.info.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.component.info.content.component.ContentPagerItem
import com.jooheon.toyplayer.features.player.component.info.content.component.ContentScrollableItem

@Composable
internal fun ContentSection(
    useScrollableItem: Boolean,
    playlists: List<Playlist>,
    currentSong: Song,
    titleAlpha: Float,
    contentAlpha: Float,
    isPlaying: Boolean,
    enableScroll: Boolean,
    onContentClick: (Playlist, startIndex: Int) -> Unit,
    onContentAlphaChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (useScrollableItem) {
        ContentScrollableItem(
            playlists = playlists,
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
            playlists = playlists,
            currentSong = currentSong,
            titleAlpha = titleAlpha,
            isPlaying = isPlaying,
            onContentClick = onContentClick,
            modifier = modifier.alpha(contentAlpha),
        )
    }
}