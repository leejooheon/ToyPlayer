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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.common.cardBottomPreviewHeight
import com.jooheon.toyplayer.features.player.common.cardTopPreviewHeight
import com.jooheon.toyplayer.features.player.common.contentSpace
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun ContentScrollableItem(
    models: List<PlayerUiState.ContentModel>,
    currentSong: Song,
    titleAlpha: Float,
    isPlaying: Boolean,
    enableScroll: Boolean,
    onContentClick: (Int, Song) -> Unit,
    onContentAlphaChanged: (Float) -> Unit,
    onOffsetChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nestedScrollState = rememberNestedScrollInteropConnection()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { scrollOffset ->
                onOffsetChanged.invoke(scrollOffset.toFloat())
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

        items(models.size) { index ->
            val model = models.getOrNull(index) ?: return@items

            ContentItem(
                state = rememberLazyListState(),
                model = model,
                currentSong = currentSong,
                titleAlpha = titleAlpha,
                isPlaying = isPlaying,
                onContentClick = onContentClick,
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
            models = uiState.contentModels,
            currentSong = song,
            titleAlpha = 1f,
            isPlaying = false,
            enableScroll = true,
            onContentAlphaChanged = {},
            onContentClick = { _, _ -> },
            onOffsetChanged = {},
        )
    }
}