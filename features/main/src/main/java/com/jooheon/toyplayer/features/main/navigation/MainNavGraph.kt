package com.jooheon.toyplayer.features.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.library.main.LibraryScreen
import com.jooheon.toyplayer.features.player.PlayerScreen
import com.jooheon.toyplayer.features.splash.SplashScreen

fun NavGraphBuilder.mainNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
) {
    composable<ScreenNavigation.Splash> {
        SplashScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Player> {
        PlayerScreen(
            navigateTo = navigateTo
        )
    }
    composable<ScreenNavigation.Library> {
        LibraryScreen(
            navigateTo = navigateTo
        )
    }
}
