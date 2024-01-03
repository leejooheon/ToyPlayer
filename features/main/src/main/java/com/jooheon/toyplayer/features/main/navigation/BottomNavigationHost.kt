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
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
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
            startDestination = ScreenNavigation.BottomSheet.Song.route
        ) {
            composable(ScreenNavigation.BottomSheet.Song.route) {
                MusicSongScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Album.route) {
                MusicAlbumScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Artist.route) {
                MusicArtistScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Cache.route) {
                MusicCacheScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Playlist.route) {
                MusicPlaylistScreen(navController)
            }
        }
    }
}

data class BottomSheetLayoutConfig(
    val sheetBackgroundColor: Color = Color.Transparent
)