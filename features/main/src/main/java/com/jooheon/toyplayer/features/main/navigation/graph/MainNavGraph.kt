package com.jooheon.toyplayer.features.main.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.library.main.LibraryScreen
import com.jooheon.toyplayer.features.player.PlayerScreen
import com.jooheon.toyplayer.features.playlist.main.PlaylistScreen


fun NavGraphBuilder.mainNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Main.Song> {
        PlayerScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Main.Playlist> {
        PlaylistScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Main.Library> {
        LibraryScreen(
            navigateTo = navigateTo
        )
    }
}
