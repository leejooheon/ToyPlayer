package com.jooheon.clean_architecture.features.musicplayer.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jooheon.clean_architecture.toyproject.features.common.compose.extensions.pagerTabIndicatorOffset
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.MusicAlbumScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.MusicArtistScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.MusicSongScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.MusicPlaylistScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.model.MusicTabScreen

import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicTabPagerScreen(
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        MusicTabScreen.entries.size
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .pagerTabIndicatorOffset(
                                pagerState = pagerState,
                                tabPositions = tabPositions
                            )
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
            ) {
                MusicTabScreen.entries.forEachIndexed { index, musicTabScreen ->
                    val selected = pagerState.currentPage == index

                    Tab(
                        selected = selected,
                        text = {
                            Text(
                                text = musicTabScreen.value.asString(),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if(selected) MaterialTheme.colorScheme.onBackground
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        },
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                    )
                }
            }

            HorizontalPager(pagerState) {
                when (MusicTabScreen.fromIndex(it)) {
                    MusicTabScreen.Song -> MusicSongScreen(navController)
                    MusicTabScreen.Album -> MusicAlbumScreen(navController)
                    MusicTabScreen.Artist -> MusicArtistScreen(navController)
                    MusicTabScreen.Playlist -> MusicPlaylistScreen(navController)
                }
            }
        }
    }
}

@Preview
@Composable
private fun MusicTabPagerScreenPreviewDark() {
    PreviewTheme(true) {
        MusicTabPagerScreen(
            navController = NavController(LocalContext.current),
        )
    }
}