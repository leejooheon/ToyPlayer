package com.jooheon.toyplayer.features.player.component.inside

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

@Composable
internal fun InsidePager(
    pagerState: PagerState,
    model: PlayerUiState.PagerModel,
    currentSong: Song,
    onSwipe: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    var userScroll by remember { mutableStateOf(false) }

    LaunchedEffect(model.items) {
        if(model.items.isEmpty()) return@LaunchedEffect
        pagerState.scrollToPage(model.currentPageIndex(currentSong.key()))
    }

    LaunchedEffect(currentSong) {
        val index = model.currentPageIndex(currentSong.key())
        if(pagerState.currentPage == index) return@LaunchedEffect
        pagerState.scrollToPage(index)
    }

    LaunchedEffect(pagerState, model) {
        combine(
            snapshotFlow { pagerState.currentPage },
            snapshotFlow { pagerState.isScrollInProgress }
        ) { page, isScrollInProgress ->
            page to isScrollInProgress
        }.collectLatest { (page, isScrollInProgress) ->
            if (userScroll && !isScrollInProgress) {
                val item = model.items
                    .getOrNull(page)
                    .takeIf { it != currentSong }
                    ?: return@collectLatest

                onSwipe.invoke(item)
            }

            userScroll = isScrollInProgress
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { index ->
        val item = model.items[index]
        InsidePagerItem(
            image = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChannelPager() {
    val uiState = PlayerUiState.preview
    val model = uiState.pagerModel
    val song = uiState.musicState.currentPlayingMusic

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { model.items.size }
    )

    ToyPlayerTheme {
        InsidePager(
            pagerState = pagerState,
            currentSong = song,
            model = model,
            onSwipe = {},
        )
    }
}