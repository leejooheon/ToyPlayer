package com.jooheon.toyplayer.features.player.component.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.common.extension.toPx
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.common.cardBottomPreviewHeight
import com.jooheon.toyplayer.features.player.common.cardTopPreviewHeight
import com.jooheon.toyplayer.features.player.common.contentSpace
import com.jooheon.toyplayer.features.player.component.info.content.ContentSection
import com.jooheon.toyplayer.features.player.component.info.control.ControlSection
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import com.jooheon.toyplayer.features.player.model.toChunkedModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min


@Composable
fun InfoSection(
    pagerState: PagerState,
    musicState: MusicState,
    currentPlaylist: Playlist,
    currentSong: Song,
    models: List<PlayerUiState.ContentModel>,
    isLoading: Boolean,
    isShow: Boolean,
    onSettingClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onContentClick: (Int, Song) -> Unit,
    onOffsetChanged: (Float) -> Unit,
) {
    val chunkedModel = models.toChunkedModel()
    var contentAlpha by remember { mutableFloatStateOf(1f) }
    val animTime = integerResource(android.R.integer.config_longAnimTime)
    val pageOffset by remember {
        derivedStateOf {
            if (pagerState.currentPage == 0) {
                min(max(pagerState.currentPageOffsetFraction * 2, 0f), 1f)
            } else 1f
        }
    }

    LaunchedEffect(pagerState){
        snapshotFlow { pagerState.currentPageOffsetFraction }
            .collectLatest { onOffsetChanged.invoke(it) }
    }

    LaunchedEffect(isShow) {
        if(!isShow) { // hide 시 첫번쨰 페이지로 이동
            try { withContext(Dispatchers.IO) { delay(animTime.toLong()) } }
            finally { pagerState.scrollToPage(0) }
        }
    }

    AnimatedVisibility(
        visible = isShow,
        enter = fadeIn(animationSpec = tween(durationMillis = animTime)),
        exit = fadeOut(animationSpec = tween(durationMillis = animTime))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            InfoBlurrySection(
                imageUrl = currentSong.imageUrl,
                contentDescription = currentSong.title,
                currentPageIndex = pagerState.currentPage
            )

            VerticalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.1f
                ),
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            translationY = calculateYOffset(
                                state = pagerState,
                                index = pageIndex,
                            )
                        )
                ) {
                    when (pageIndex) {
                        0 -> ControlSection(
                            musicState = musicState,
                            playlist = currentPlaylist,
                            titleAlpha = 1 - pageOffset,
                            isLoading = isLoading,
                            onSettingClick = onSettingClick,
                            onPlayPauseClick = onPlayPauseClick,
                        )
                        else -> {
//                            Timber.d("index: $pageIndex / ${chunkedModel.size}")
                            val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

                            val newModels = chunkedModel
                                .getOrNull(pageIndex - 1)
                                .takeIf { it.defaultEmpty().isNotEmpty() }
                                ?: return@Box

                            ContentSection(
                                useScrollableItem = chunkedModel.size == 1 || newModels.size > 4,
                                models = newModels,
                                currentSong = currentSong,
                                titleAlpha = pageOffset,
                                contentAlpha = contentAlpha,
                                isPlaying = musicState.isPlaying(),
                                enableScroll = isLastPage,
                                onContentClick = onContentClick,
                                onContentAlphaChanged = { alpha -> contentAlpha = alpha },
                                modifier = Modifier
                                    .zIndex((pagerState.pageCount - pageIndex).toFloat())
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun calculateYOffset(state: PagerState, index: Int): Float {
    // NOTE
    // 실제로 보여야하는 부분은 cardTopPreviewHeight 인데
    // item 상단에 cardBottomPreviewHeight, contentSpace 가 상단에 있으므로 이것도 더해야함
    // item = (ContentPagerItem, ContentScrollableItem)
    val heightPx = (cardTopPreviewHeight() + cardBottomPreviewHeight() + contentSpace()).toPx()
    val pageOffset = state.currentPage - index + state.currentPageOffsetFraction
    val offset = pageOffset * -heightPx
//    Timber.d("calculateYOffset: $offset, $heightPx")
    return -offset
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
private fun PreviewInfoSection() {
    val uiState = PlayerUiState.preview
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uiState.contentModels.chunked(4).size + 1 },
    )

    ToyPlayerTheme {
        InfoSection(
            pagerState = pagerState,
            musicState = uiState.musicState,
            currentSong = uiState.musicState.currentPlayingMusic,
            currentPlaylist = uiState.pagerModel.currentPlaylist,
            models = uiState.contentModels,
            isLoading = false,
            isShow = true,
            onSettingClick = {},
            onPlayPauseClick = {},
            onContentClick = { _, _ -> },
            onOffsetChanged = {},
        )
    }
}