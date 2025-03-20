package com.jooheon.toyplayer.features.main.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.main.navigation.MainNavigator
import com.jooheon.toyplayer.features.album.navigation.albumNavGraph
import com.jooheon.toyplayer.features.artist.navigation.artistNavGraph
import com.jooheon.toyplayer.features.main.navigation.mainNavGraph
import com.jooheon.toyplayer.features.playlist.navigation.playlistNavGraph
import com.jooheon.toyplayer.features.settings.navigation.settingNavGraph

@Composable
internal fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    onChangeDarkTheme: (Boolean) -> Unit,
) {
    val navController = navigator.navController

    val navigateTo: (ScreenNavigation) -> Unit = { destination ->
        when (destination) {
            is ScreenNavigation.Player -> {
                navigator.navController.navigate(ScreenNavigation.Player) {
                    launchSingleTop = true
                    popUpTo(ScreenNavigation.Splash) {
                        inclusive = true
                    }
                }
            }
            is ScreenNavigation.Back -> navigator.popBackStack()
            else -> navigator.navController.navigate(destination)
        }
    }

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination,
        modifier = modifier
    ) {
        mainNavGraph(
            navigateTo = navigateTo
        )
        artistNavGraph(
            navigateTo = navigateTo
        )
        albumNavGraph(
            navigateTo = navigateTo
        )
        playlistNavGraph(
            navigateTo = navigateTo
        )
        settingNavGraph(
            navigateTo = navigateTo,
            onChangeDarkTheme = onChangeDarkTheme,
        )
    }
}