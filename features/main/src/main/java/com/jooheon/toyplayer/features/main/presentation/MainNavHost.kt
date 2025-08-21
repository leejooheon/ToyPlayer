package com.jooheon.toyplayer.features.main.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.SinglePaneSceneStrategy
import com.jooheon.toyplayer.core.navigation.NavMapper
import com.jooheon.toyplayer.core.navigation.or
import com.jooheon.toyplayer.features.album.navigation.albumNavGraph
import com.jooheon.toyplayer.features.artist.navigation.artistNavGraph
import com.jooheon.toyplayer.features.main.navigation.MainNavigator
import com.jooheon.toyplayer.features.main.navigation.mainNavGraph
import com.jooheon.toyplayer.features.playlist.navigation.playlistNavGraph
import com.jooheon.toyplayer.features.settings.navigation.settingsNavGraph

@Composable
internal fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    onChangeDarkTheme: (Boolean) -> Unit,
) {
    val navMapper: NavMapper = remember {
        mainNavGraph(
            navigateTo = { navigator.navigateTo(it) },
            onBack = { navigator.popBackStack() },
        ) or settingsNavGraph(
            navigateTo = { navigator.navigateTo(it) },
            onBack = { navigator.popBackStack() },
        ) or albumNavGraph (
            onBack = { navigator.popBackStack() },
        ) or artistNavGraph (
            navigateTo = { navigator.navigateTo(it) },
            onBack = { navigator.popBackStack() },
        ) or playlistNavGraph (
            navigateTo = { navigator.navigateTo(it) },
            onBack = { navigator.popBackStack() },
        )
    }

    NavDisplay(
        modifier = modifier,
        backStack = navigator.backStack,
        onBack = { navigator.popBackStack() },
        sceneStrategy = SinglePaneSceneStrategy(),
        entryProvider = { key ->
            navMapper(key) ?: error("Unknown route: $key")
        }
    )
}