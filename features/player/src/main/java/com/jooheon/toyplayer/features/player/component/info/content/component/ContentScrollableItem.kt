package com.jooheon.toyplayer.features.player.component.info.content.component

import android.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.common.cardBottomPreviewHeight
import com.jooheon.toyplayer.features.player.common.cardTopPreviewHeight
import com.jooheon.toyplayer.features.player.common.contentSpace
import com.jooheon.toyplayer.features.player.component.info.content.ContentItem
import com.jooheon.toyplayer.features.player.model.PlayerEvent
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

@Composable
internal fun ContentScrollableItem(
    playlists: List<Playlist>,
    currentSong: Song,
    titleAlpha: Float,
    enableScroll: Boolean,
    isPlaying: Boolean,
    isShow: Boolean,
    onContentClick: (Playlist, startIndex: Int) -> Unit,
    onFavoriteClick: (playlistId: Int, song: Song) -> Unit,
    onDetailsClick: (playlistId: Int) -> Unit,
    onContentAlphaChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val animTime = integerResource(android.R.integer.config_longAnimTime)
    val nestedScrollState = rememberNestedScrollInteropConnection()
    val listState = rememberLazyListState()

    LaunchedEffect(isShow) {
        if(!isShow) { // hide 시 첫번쨰 페이지로 이동
            try { withContext(Dispatchers.IO) { delay(animTime.toLong()) } }
            finally { listState.scrollToItem(0, 0) }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { scrollOffset ->
                if (listState.firstVisibleItemIndex != 0) return@collect
                val alpha = when {
                    scrollOffset < 1 -> 1f
                    scrollOffset > 100 -> 0f
                    else -> 1f - (scrollOffset / 100f)
                }
                onContentAlphaChanged.invoke(alpha)
            }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(0.dp),
        userScrollEnabled = enableScroll,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollState)
    ) {
        item {
            Spacer(modifier = Modifier.height(cardBottomPreviewHeight()))
            Spacer(modifier = Modifier.height(contentSpace()))
        }

        items(playlists.size) { index ->
            val playlist = playlists.getOrNull(index) ?: return@items

            ContentItem(
                state = rememberLazyListState(),
                playlist = playlist,
                currentSong = currentSong,
                titleAlpha = titleAlpha,
                isPlaying = isPlaying,
                onContentClick = { onContentClick.invoke(playlist, it) },
                onFavoriteClick = { onFavoriteClick.invoke(playlist.id, it)},
                onDetailsClick = { onDetailsClick.invoke(playlist.id) }
            )

            Spacer(modifier = Modifier.height(contentSpace()))
        }

        item {
            Spacer(modifier = Modifier.height(cardTopPreviewHeight()))
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = Color.GRAY.toLong()
)
@Composable
private fun PreviewContentScrollableSection() {
    val uiState = PlayerUiState.preview
    val song = uiState.musicState.currentPlayingMusic
    ToyPlayerTheme {
        ContentScrollableItem(
            playlists = uiState.playlists,
            currentSong = song,
            titleAlpha = 1f,
            enableScroll = true,
            isPlaying = false,
            isShow = true,
            onContentAlphaChanged = {},
            onContentClick = { _, _ -> },
            onFavoriteClick = { _, _ -> },
            onDetailsClick = {},
        )
    }
}