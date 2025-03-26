package com.jooheon.toyplayer.features.main.presentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jooheon.toyplayer.features.album.navigation.albumNavGraph
import com.jooheon.toyplayer.features.artist.navigation.artistNavGraph
import com.jooheon.toyplayer.features.main.navigation.MainNavigator
import com.jooheon.toyplayer.features.main.navigation.mainNavGraph
import com.jooheon.toyplayer.features.playlist.navigation.playlistNavGraph
import com.jooheon.toyplayer.features.settings.navigation.settingNavGraph
import timber.log.Timber

@Composable
internal fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    onChangeDarkTheme: (Boolean) -> Unit,
) {
    val currentBackStackEntry by navigator.navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        Timber.d("Current route: ${currentBackStackEntry?.destination?.route}")
    }
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300))  },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        mainNavGraph(
            navigateTo = navigator.navigateTo
        )
        artistNavGraph(
            navigateTo = navigator.navigateTo
        )
        albumNavGraph(
            navigateTo = navigator.navigateTo
        )
        playlistNavGraph(
            navigateTo = navigator.navigateTo
        )
        settingNavGraph(
            navigateTo = navigator.navigateTo,
            onChangeDarkTheme = onChangeDarkTheme,
        )
    }
}