package com.jooheon.clean_architecture.features.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.common.compose.observeWithLifecycle
import com.jooheon.clean_architecture.features.github.main.presentation.main.GithubScreen
import com.jooheon.clean_architecture.features.github.main.presentation.detail.GithubDetailScreen
import com.jooheon.clean_architecture.features.github.main.presentation.detail.GithubDetailScreenViewModel
import com.jooheon.clean_architecture.features.github.main.model.GithubScreenEvent
import com.jooheon.clean_architecture.features.github.main.presentation.main.GithubScreenViewModel
import com.jooheon.clean_architecture.features.main.MainScreen
import com.jooheon.clean_architecture.features.map.MapScreen
import com.jooheon.clean_architecture.features.musicplayer.screen.MusicTabPagerScreen
import com.jooheon.clean_architecture.features.wikipedia.WikipediaScreen
import com.jooheon.clean_architecture.features.wikipedia.detail.WikipediaDatailScreen
import com.jooheon.clean_architecture.features.setting.SettingScreen
import com.jooheon.clean_architecture.features.setting.equalizer.EqualizerScreen
import com.jooheon.clean_architecture.features.setting.theme.ThemeScreen
import com.jooheon.clean_architecture.features.splash.SplashScreen

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
            startDestination = ScreenNavigation.BottomSheet.Github.route
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
                WikipediaScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Map.route) {
                MapScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Search.route) {
                MusicTabPagerScreen(navigator)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
internal fun FullScreenNavigationHost(
    modifier: Modifier = Modifier
) {
    val bottomSheetLayoutConfig by remember { mutableStateOf(BottomSheetLayoutConfig()) }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = SwipeableDefaults.AnimationSpec,
        skipHalfExpanded = true
    )

    val bottomSheetNavigator = remember(modalBottomSheetState) {
        BottomSheetNavigator(sheetState = modalBottomSheetState)
    }

    val navController = rememberNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetBackgroundColor = bottomSheetLayoutConfig.sheetBackgroundColor,
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0),
        )
    ) {
        NavHost(
            navController = navController,
            startDestination = ScreenNavigation.Splash.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(ScreenNavigation.Splash.route) {
                SplashScreen(
                    navigator = navController
                )
            }

            composable(ScreenNavigation.Main.route) {
                MainScreen(
                    navigator = navController
                )
            }
            composable(ScreenNavigation.Setting.Main.route) {
                SettingScreen(navigator = navController)
            }

            composable(
                route = ScreenNavigation.Subway.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =  ScreenNavigation.Subway.WidgetDeeplink
                    }
                )
            ) {
//                TestScreen()
            }

            composable(ScreenNavigation.Setting.Theme.route) {
                ThemeScreen(navigator = navController)
            }

            composable(ScreenNavigation.Setting.Equalizer.route) {
                EqualizerScreen(navigator = navController)
            }

            composable(
                route = ScreenNavigation.Detail.GithubDetail.route,
                arguments = ScreenNavigation.Detail.GithubDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val repository = ScreenNavigation.Detail.GithubDetail.parseRepository(arguments)
                val githubId = ScreenNavigation.Detail.GithubDetail.parseGithubId(arguments)

                val viewModel = hiltViewModel<GithubDetailScreenViewModel>().apply {
                    initState(
                        id = githubId,
                        item = repository
                    )
                }
                GithubDetailScreen(
                    state = viewModel.state
                )
            }

            composable(
                route = ScreenNavigation.Detail.WikipediaDetail.route,
                arguments = ScreenNavigation.Detail.WikipediaDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val page = ScreenNavigation.Detail.WikipediaDetail.parsePage(arguments)
                WikipediaDatailScreen(keyword = page.title ?: "")
            }
        }
    }
}


data class BottomSheetLayoutConfig(
    val sheetBackgroundColor: Color = Color.Transparent
)