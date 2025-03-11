package com.jooheon.toyplayer.features.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.main.navigation.MainNavigator
import com.jooheon.toyplayer.features.main.navigation.graph.artistNavGraph
import com.jooheon.toyplayer.features.main.navigation.graph.detailsNavGraph
import com.jooheon.toyplayer.features.main.navigation.graph.mainNavGraph
import com.jooheon.toyplayer.features.main.navigation.graph.settingsNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    modifier: Modifier = Modifier,
) {
    val navController = navigator.navController

    val navigateTo: (ScreenNavigation) -> Unit = { destination ->
        when (destination) {
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
        detailsNavGraph(
            navigateTo = navigateTo
        )
        settingsNavGraph(
            navigateTo = navigateTo
        )
    }
}