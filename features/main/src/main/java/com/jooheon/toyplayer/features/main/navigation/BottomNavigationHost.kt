package com.jooheon.toyplayer.features.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.MusicAlbumScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.MusicArtistScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.MusicCacheScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.MusicPlaylistScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.MusicSongScreen

@ExperimentalComposeUiApi
@Composable
internal fun BottomNavigationHost(
    navController: NavHostController,
    navigator: NavController,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = ScreenNavigation.Bottom.Song
        ) {
            composable<ScreenNavigation.Bottom.Song> {
                MusicSongScreen(navigator)
            }
            composable<ScreenNavigation.Bottom.Album> {
                MusicAlbumScreen(navigator)
            }
            composable<ScreenNavigation.Bottom.Artist> {
                MusicArtistScreen(navigator)
            }
            composable<ScreenNavigation.Bottom.Cache> {
                MusicCacheScreen(navigator)
            }
            composable<ScreenNavigation.Bottom.Playlist> {
                MusicPlaylistScreen(navigator)
            }
        }
    }
}

data class BottomSheetLayoutConfig(
    val sheetBackgroundColor: Color = Color.Transparent
)