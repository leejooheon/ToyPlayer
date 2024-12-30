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
import com.jooheon.toyplayer.features.musicplayer.navigation.musicNavGraph
import com.jooheon.toyplayer.features.musicplayer.navigation.songNavGraph
import com.jooheon.toyplayer.features.setting.navigation.settingNavGraph

@Composable
internal fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val navController = navigator.navController
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.surfaceDim)
    ) {
        NavHost(
            navController = navController,
            startDestination = navigator.startDestination,
        ) {
            songNavGraph(
                navigate = { navigator.navController.navigate(it) }
            )
            musicNavGraph(
                navigate = { navigator.navController.navigate(it) },
                onBackClick = { navigator.navController.popBackStack() }
            )
            settingNavGraph(
                navigate = { navigator.navController.navigate(it) },
                onBackClick = { navigator.navController.popBackStack() }
            )
        }
    }
}