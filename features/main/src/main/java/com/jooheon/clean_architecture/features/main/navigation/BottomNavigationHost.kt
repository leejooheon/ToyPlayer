package com.jooheon.clean_architecture.features.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.toyproject.features.common.compose.observeWithLifecycle
import com.jooheon.clean_architecture.toyproject.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.clean_architecture.features.github.main.presentation.main.GithubScreen
import com.jooheon.clean_architecture.features.github.main.model.GithubScreenEvent
import com.jooheon.clean_architecture.features.github.main.presentation.main.GithubScreenViewModel
import com.jooheon.clean_architecture.features.map.presentation.MapScreen
import com.jooheon.clean_architecture.features.map.presentation.MapViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.MusicTabPagerScreen
import com.jooheon.clean_architecture.features.wikipedia.presentation.WikipediaScreen
import com.jooheon.clean_architecture.features.wikipedia.model.WikipediaScreenEvent
import com.jooheon.clean_architecture.features.wikipedia.presentation.WikipediaScreenViewModel

@ExperimentalComposeUiApi
@Composable
internal fun BottomNavigationHost(
    navController: NavHostController,
    navigator: NavController,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = ScreenNavigation.BottomSheet.Music.route
        ) {
            composable(ScreenNavigation.BottomSheet.Github.route) {
                val viewModel = hiltViewModel<GithubScreenViewModel>().apply {
                    navigateToGithubDetailScreen.observeWithLifecycle {
                        GithubScreenEvent.navigateToDetailScreen(navigator, it)
                    }
                }
                GithubScreen(
                    state = viewModel.githubState,
                    onEvent = viewModel::dispatch,
                )
            }
            composable(ScreenNavigation.BottomSheet.Wiki.route) {
                val viewModel = hiltViewModel<WikipediaScreenViewModel>().apply {
                    navigateToWikipediaDetailScreen.observeWithLifecycle {
                        WikipediaScreenEvent.navigateToDetailScreen(navigator, it)
                    }
                }
                WikipediaScreen(
                    state = viewModel.state,
                    onEvent = viewModel::dispatch
                )
            }
            composable(ScreenNavigation.BottomSheet.Map.route) {
                val viewModel = hiltViewModel<MapViewModel>()
                val state by viewModel.mapState.collectAsStateWithLifecycle()
                MapScreen(
                    state = state,
                    onEvent = viewModel::dispatch
                )
            }
            composable(ScreenNavigation.BottomSheet.Music.route) {
                MusicTabPagerScreen(
                    navController = navigator,
                )
            }
        }
    }
}

data class BottomSheetLayoutConfig(
    val sheetBackgroundColor: Color = Color.Transparent
)