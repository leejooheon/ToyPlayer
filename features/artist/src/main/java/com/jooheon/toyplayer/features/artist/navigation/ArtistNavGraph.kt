package com.jooheon.toyplayer.features.artist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.artist.details.ArtistDetailScreen
import com.jooheon.toyplayer.features.artist.more.ArtistMoreScreen

fun NavGraphBuilder.artistNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Artist.More> {
        ArtistMoreScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Artist.Details> {
        val args = it.toRoute<ScreenNavigation.Artist.Details>()
        ArtistDetailScreen(
            navigateTo = navigateTo,
            artistId = args.artistId,
        )
    }
}