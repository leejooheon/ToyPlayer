package com.jooheon.toyplayer.features.main.navigation

import androidx.navigation3.runtime.NavEntry
import com.jooheon.toyplayer.core.navigation.NavMapper
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.library.main.LibraryScreen
import com.jooheon.toyplayer.features.player.PlayerScreen
import com.jooheon.toyplayer.features.splash.SplashScreen

fun mainNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
    onBack: () -> Unit,
): NavMapper = { key ->
    when (key) {
        is ScreenNavigation.Splash  -> NavEntry(key) {
            SplashScreen(
                navigateTo = navigateTo
            )
        }
        is ScreenNavigation.Player  -> NavEntry(key) {
            PlayerScreen(
                navigateTo = navigateTo
            )
        }
        is ScreenNavigation.Library -> NavEntry(key) {
            LibraryScreen(
                navigateTo = navigateTo,
                onBack = onBack
            )
        }
        else -> null
    }
}