package com.jooheon.toyplayer.features.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.MusicAlbumScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.MusicArtistScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.MusicCacheScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.MusicPlaylistScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.MusicSongScreen

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val navController = navigator.navController
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.surfaceDim)
    ) {
        NavHost(
            navController = navController,
            startDestination = navigator.startDestination,
        ) {
            composable<ScreenNavigation.Main.Song> {
                MusicSongScreen(navController)
            }
            composable<ScreenNavigation.Main.Album> {
                MusicAlbumScreen(navController)
            }
            composable<ScreenNavigation.Main.Artist> {
                MusicArtistScreen(navController)
            }
            composable<ScreenNavigation.Main.Cache> {
                MusicCacheScreen(navController)
            }
            composable<ScreenNavigation.Main.Playlist> {
                MusicPlaylistScreen(navController)
            }
        }
    }
}