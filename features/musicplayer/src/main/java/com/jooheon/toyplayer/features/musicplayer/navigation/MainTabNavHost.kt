package com.jooheon.toyplayer.features.musicplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.album.MusicAlbumScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.MusicArtistScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.cache.MusicCacheScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.MusicPlaylistScreen
import com.jooheon.toyplayer.features.player.PlayerScreen

fun NavGraphBuilder.mainTabNavGraph(
    navigate: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Main.Song> {
        PlayerScreen(
            onBackPressed = {}
        )

//        MusicSongScreen(
//            navigate = navigate
//        )
    }
    composable<ScreenNavigation.Main.Album> {
        MusicAlbumScreen(
            navigate = navigate
        )
    }
    composable<ScreenNavigation.Main.Artist> {
        MusicArtistScreen(
            navigate = navigate
        )
    }
    composable<ScreenNavigation.Main.Cache> {
        MusicCacheScreen(
            navigate = navigate
        )
    }
    composable<ScreenNavigation.Main.Playlist> {
        MusicPlaylistScreen(
            navigate = navigate
        )
    }
}
