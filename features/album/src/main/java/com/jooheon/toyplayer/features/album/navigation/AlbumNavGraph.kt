package com.jooheon.toyplayer.features.album.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.album.details.AlbumDetailScreen
import com.jooheon.toyplayer.features.album.more.AlbumMoreScreen

fun NavGraphBuilder.albumNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Album.More> {
        AlbumMoreScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Album.Details> {
        val args = it.toRoute<ScreenNavigation.Album.Details>()
        AlbumDetailScreen(
            navigateTo = navigateTo,
            albumId = args.albumId,
        )
    }
}