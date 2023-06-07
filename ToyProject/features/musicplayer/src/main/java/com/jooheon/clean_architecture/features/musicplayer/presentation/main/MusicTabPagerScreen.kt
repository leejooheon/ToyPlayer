package com.jooheon.clean_architecture.features.musicplayer.presentation.main

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.extensions.pagerTabIndicatorOffset
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.MusicAlbumScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.MusicArtistScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.MusicSongScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.MusicPlaylistScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenState
import com.jooheon.clean_architecture.features.musicservice.data.MusicState

import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicTabPagerScreen(
    musicPlayerScreenState: MusicPlayerScreenState,
    musicAlbumScreenState: MusicAlbumScreenState,
    musicArtistScreenState: MusicArtistScreenState,
    musicPlaylistScreenState: MusicPlaylistScreenState,

    onMusicPlayerScreenEvent: (MusicPlayerScreenEvent) -> Unit,
    onMusicAlbumScreenEvent: (MusicAlbumScreenEvent) -> Unit,
    onMusicArtistScreenEvent: (MusicArtistScreenEvent) -> Unit,
    onMusicPlaylistScreenEvent: (MusicPlaylistScreenEvent) -> Unit,

    onMusicPlaylistItemEvent: (MusicPlaylistItemEvent) -> Unit,
    onMusicMediaItemEvent: (MusicMediaItemEvent) -> Unit,
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
                        MusicSongScreen(
                            musicPlayerScreenState = musicPlayerScreenState,
                            onEvent = onMusicPlayerScreenEvent,
                            onMusicMediaItemEvent = onMusicMediaItemEvent
                        )
                    }
                    1 -> {
                        MusicAlbumScreen(
                            musicAlbumState = musicAlbumScreenState,
                            musicPlayerScreenState = musicPlayerScreenState,
                            onMusicAlbumEvent = onMusicAlbumScreenEvent,
                            onMusicPlayerScreenEvent = onMusicPlayerScreenEvent,
                        )
                    }
                    2 -> {
                        MusicArtistScreen(
                            musicArtistState = musicArtistScreenState,
                            musicPlayerScreenState = musicPlayerScreenState,
                            onMusicArtistScreenEvent = onMusicArtistScreenEvent,
                            onMusicPlayerScreenEvent = onMusicPlayerScreenEvent,
                        )
                    }
                    3 -> {
                        MusicPlaylistScreen(
                            musicPlaylistScreenState = musicPlaylistScreenState,
                            musicPlayerScreenState = musicPlayerScreenState,
                            onMusicPlayerScreenEvent = onMusicPlayerScreenEvent,
                            onMusicPlaylistScreenEvent = onMusicPlaylistScreenEvent,
                            onMusicPlaylistItemEvent = onMusicPlaylistItemEvent
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
        MusicTabPagerScreen(
            musicPlayerScreenState = MusicPlayerScreenState.default.copy(
                musicState = MusicState(
                    playlist = Song.defaultList
                )
            ),
            musicAlbumScreenState = MusicAlbumScreenState.default,
            musicArtistScreenState = MusicArtistScreenState.default,
            musicPlaylistScreenState = MusicPlaylistScreenState.default,

            onMusicPlayerScreenEvent = {},
            onMusicAlbumScreenEvent = {},
            onMusicArtistScreenEvent = {},
            onMusicPlaylistScreenEvent = {},

            onMusicPlaylistItemEvent = {},
            onMusicMediaItemEvent = {},
        )
    }
}