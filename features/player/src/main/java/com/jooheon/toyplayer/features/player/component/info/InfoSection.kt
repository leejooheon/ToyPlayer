package com.jooheon.toyplayer.features.player.component.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.common.contentHeight
import com.jooheon.toyplayer.features.player.component.info.content.ContentPagerItem
import com.jooheon.toyplayer.features.player.component.info.content.wholeContentSectionHeight
import com.jooheon.toyplayer.features.player.component.info.control.ControlSection
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import com.jooheon.toyplayer.features.player.model.toChunkedModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min


@Composable
fun InfoSection(
    pagerState: PagerState,
    musicState: MusicState,
    currentPosition: Long,
    playedName: String,
    playedThumbnailImage: String,
    currentSong: Song,
    playlists: List<Playlist>,
    isLoading: Boolean,
    isShow: Boolean,
    onLibraryClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    onSettingClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick:() -> Unit,
    onPreviousClick: () -> Unit,
    onContentClick: (playlist: Playlist, startIndex: Int) -> Unit,
    onFavoriteClick: (playlistId: Int, song: Song) -> Unit,
    onDetailsClick: (playlistId: Int) -> Unit,
    onSeek: (Long) -> Unit,
) {
    val chunkedModel = playlists.toChunkedModel()
    val animTime = integerResource(android.R.integer.config_longAnimTime)
    val pageOffset by remember {
        derivedStateOf {
            min(max(pagerState.currentPageOffsetFraction * 2, 0f), 1f)
        }
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
                        .zIndex(if (pageIndex == 0) Float.MAX_VALUE else 0f)
                        .graphicsLayer(translationY = calculateYOffset(pageIndex, pagerState))
                ) {
                    when (pageIndex) {
                        0 -> ControlSection(
                            musicState = musicState,
                            currentPosition = currentPosition,
                            playedName = playedName,
                            playedThumbnailImage = playedThumbnailImage,
                            titleAlpha = 1 - pageOffset,
                            isLoading = isLoading,
                            onLibraryClick = onLibraryClick,
                            onPlaylistClick = onPlaylistClick,
                            onSettingClick = onSettingClick,
                            onPlayPauseClick = onPlayPauseClick,
                            onNextClick = onNextClick,
                            onPreviousClick = onPreviousClick,
                            onSeek = onSeek,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.safeDrawing)
                        )
                        else -> {
                            val newModels = chunkedModel
                                .getOrNull(pageIndex - 1)
                                .takeIf { it.defaultEmpty().isNotEmpty() }
                                ?: return@Box
                            LaunchedEffect(pagerState.currentPage) { }
                            ContentPagerItem(
                                playlists = newModels,
                                currentSong = currentSong,
                                titleAlpha = if(pagerState.currentPage != 0) 1f else pageOffset,
                                isPlaying = musicState.isPlaying(),
                                onContentClick = onContentClick,
                                onFavoriteClick = onFavoriteClick,
                                onDetailsClick = onDetailsClick,
                                modifier = Modifier
                                    .alpha(
                                        alpha = when {
                                            pagerState.currentPage == pageIndex -> 1f - pageOffset
                                            pagerState.currentPage < pageIndex -> 1f
                                            else -> 0f
                                        }
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun calculateYOffset(page: Int, pagerState: PagerState): Float {
    val pageOffset = pagerState.currentPage - page + pagerState.currentPageOffsetFraction
    val offset = pageOffset * -calculateOffsetBetweenPages()
    return -offset
}

@Composable
private fun calculateOffsetBetweenPages(): Int {
    val offsetBetweenPages = with(LocalDensity.current) {
        val height = (contentHeight() * 0.4f).toPx() // 카드 height의 40%만 보여야함
        height + wholeContentSectionHeight().toPx()
    }
    return offsetBetweenPages.toInt()
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
        pageCount = { uiState.playlists.chunked(4).size + 1 },
    )

    ToyPlayerTheme {
        InfoSection(
            pagerState = pagerState,
            musicState = uiState.musicState,
            currentPosition = 0L,
            playedName = UiText.StringResource(Strings.placeholder_long).asString(),
            playedThumbnailImage = "",
            currentSong = uiState.musicState.currentPlayingMusic,
            playlists = uiState.playlists,
            isLoading = false,
            isShow = true,
            onLibraryClick = {},
            onPlaylistClick = {},
            onSettingClick = {},
            onPlayPauseClick = {},
            onNextClick = {},
            onPreviousClick = {},
            onContentClick = { _, _ -> },
            onFavoriteClick = { _, _ -> },
            onDetailsClick = {},
            onSeek = {},
        )
    }
}