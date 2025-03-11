package com.jooheon.toyplayer.features.playlist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.playlist.details.PlaylistDetailScreen
import com.jooheon.toyplayer.features.playlist.main.PlaylistScreen

fun NavGraphBuilder.playlistNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Playlist.Main> {
        PlaylistScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Playlist.Details> {
        val args = it.toRoute<ScreenNavigation.Playlist.Details>()
        PlaylistDetailScreen(
            navigateTo = navigateTo,
            playlistId = args.playlistId,
        )
    }
}