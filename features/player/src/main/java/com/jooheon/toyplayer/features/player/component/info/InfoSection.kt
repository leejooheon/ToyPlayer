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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.commonui.ext.toPx
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.common.cardBottomPreviewHeight
import com.jooheon.toyplayer.features.player.common.cardTopPreviewHeight
import com.jooheon.toyplayer.features.player.common.contentSize
import com.jooheon.toyplayer.features.player.common.contentSpace
import com.jooheon.toyplayer.features.player.component.info.content.ContentSection
import com.jooheon.toyplayer.features.player.component.info.control.ControlSection
import com.jooheon.toyplayer.features.player.model.PlayerUiState
import com.jooheon.toyplayer.features.player.model.toChunkedModel
import kotlin.math.max
import kotlin.math.min


@Composable
fun InfoSection(
    pagerState: PagerState,
    musicState: MusicState,
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
) {
    val chunkedModel = playlists.toChunkedModel()
    var contentAlpha by remember { mutableFloatStateOf(1f) }
    val animTime = integerResource(android.R.integer.config_longAnimTime)
    val pageOffset by remember {
        derivedStateOf {
            if (pagerState.currentPage == 0) {
                min(max(pagerState.currentPageOffsetFraction * 2, 0f), 1f)
            } else 1f
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
                        )
                        else -> {
                            val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

                            val newModels = chunkedModel
                                .getOrNull(pageIndex - 1)
                                .takeIf { it.defaultEmpty().isNotEmpty() }
                                ?: return@Box

                            ContentSection(
                                useScrollableItem = chunkedModel.size == 1 || newModels.size > contentSize,
                                playlists = newModels,
                                currentSong = currentSong,
                                titleAlpha = pageOffset,
                                contentAlpha = contentAlpha,
                                enableScroll = isLastPage,
                                onContentClick = onContentClick,
                                onFavoriteClick = onFavoriteClick,
                                onDetailsClick = onDetailsClick,
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
        pageCount = { uiState.playlists.chunked(4).size + 1 },
    )

    ToyPlayerTheme {
        InfoSection(
            pagerState = pagerState,
            musicState = uiState.musicState,
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
            onDetailsClick = {}
        )
    }
}