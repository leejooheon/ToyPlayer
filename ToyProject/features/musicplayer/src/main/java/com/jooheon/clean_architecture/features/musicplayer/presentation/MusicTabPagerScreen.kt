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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.common.compose.extensions.pagerTabIndicatorOffset
import com.jooheon.clean_architecture.features.common.compose.observeWithLifecycle
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.MusicAlbumScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.MusicAlbumScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.MusicArtistScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.MusicArtistScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.MusicSongScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.MusicPlaylistScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.MusicPlaylistScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.MusicSongScreenViewModel

import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicTabPagerScreen(
    navController: NavController,
) {

    val tabPages = listOf(
        stringResource(id = R.string.tab_name_song ),
        stringResource(id = R.string.tab_name_album),
        stringResource(id = R.string.tab_name_artist),
        stringResource(id = R.string.tab_name_playlist)
    )

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val scrollToPage: (Int) -> Unit = { page ->
        scope.launch { pagerState.animateScrollToPage(page) }
        Unit
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
                tabPages.forEachIndexed { i, title ->
                    val selected = pagerState.currentPage == i

                    Tab(
                        selected = selected,
                        text = {
                            Text(
                                text = title,
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
                            scrollToPage(i)
                        }
                    )
                }
            }

            HorizontalPager(
                pageCount = 4,
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        val viewModel = hiltViewModel<MusicSongScreenViewModel>()
                        val screenState by viewModel.musicPlayerScreenState.collectAsStateWithLifecycle()
                        val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

                        MusicSongScreen(
                            musicSongState = screenState,
                            onMusicSongEvent = viewModel::dispatch,
                            onMusicMediaItemEvent = viewModel::onMusicMediaItemEvent,

                            musicPlayerState = musicPlayerState,
                            onMusicPlayerEvent = viewModel::dispatch,
                        )
                    }
                    1 -> {
                        val viewModel = hiltViewModel<MusicAlbumScreenViewModel>().apply {
                            navigateToDetailScreen.observeWithLifecycle {
                                navController.navigate(ScreenNavigation.Music.AlbumDetail.createRoute(it))
                            }
                        }
                        val screenState by viewModel.musicAlbumScreenState.collectAsStateWithLifecycle()
                        val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

                        MusicAlbumScreen(
                            musicAlbumState = screenState,
                            onMusicAlbumEvent = viewModel::dispatch,

                            musicPlayerState = musicPlayerState,
                            onMusicPlayerEvent = viewModel::dispatch,
                        )
                    }
                    2 -> {
                        val viewModel = hiltViewModel<MusicArtistScreenViewModel>().apply {
                            navigateToDetailScreen.observeWithLifecycle {
                                navController.navigate(ScreenNavigation.Music.ArtistDetail.createRoute(it))
                            }
                        }
                        val screenState by viewModel.musicArtistScreenState.collectAsStateWithLifecycle()
                        val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

                        MusicArtistScreen(
                            musicArtistState = screenState,
                            onMusicArtistScreenEvent = viewModel::dispatch,

                            musicPlayerState = musicPlayerState,
                            onMusicPlayerEvent = viewModel::dispatch,
                        )
                    }
                    3 -> {
                        val viewModel = hiltViewModel<MusicPlaylistScreenViewModel>().apply {
                            navigateToDetailScreen.observeWithLifecycle {
                                navController.navigate(ScreenNavigation.Music.PlaylistDetail.createRoute(it))
                            }
                        }
                        val state by viewModel.musicPlaylistScreenState.collectAsStateWithLifecycle()
                        val musicPlayerState by viewModel.musicPlayerState.collectAsStateWithLifecycle()

                        MusicPlaylistScreen(
                            musicPlaylistScreenState = state,
                            onMusicPlaylistScreenEvent = viewModel::dispatch,
                            onMusicPlaylistItemEvent = viewModel::onMusicMediaItemEvent,

                            musicPlayerState = musicPlayerState,
                            onMusicPlayerEvent = viewModel::dispatch,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MusicTabPagerScreenPreviewDark() {
    PreviewTheme(true) {
//        MusicTabPagerScreen(
//            navController = NavController(LocalContext.current),
//        )
    }
}