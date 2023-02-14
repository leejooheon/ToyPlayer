package com.jooheon.clean_architecture.presentation.view.navigation

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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jooheon.clean_architecture.presentation.view.main.MainScreen
import com.jooheon.clean_architecture.presentation.view.main.common.MusicPlayerScreen
import com.jooheon.clean_architecture.presentation.view.main.github.HomeScreen
import com.jooheon.clean_architecture.presentation.view.main.github.RepositoryDetailScreen
import com.jooheon.clean_architecture.presentation.view.main.map.MapScreen
import com.jooheon.clean_architecture.presentation.view.main.music.PlayListScreen
import com.jooheon.clean_architecture.presentation.view.main.search.ExoPlayerScreen
import com.jooheon.clean_architecture.presentation.view.main.wikipedia.WikipediaDatailScreen
import com.jooheon.clean_architecture.presentation.view.main.wikipedia.WikipediaScreen
import com.jooheon.clean_architecture.presentation.view.setting.SettingScreen
import com.jooheon.clean_architecture.presentation.view.setting.equalizer.EqualizerScreen
import com.jooheon.clean_architecture.presentation.view.setting.theme.ThemeScreen
import com.jooheon.clean_architecture.presentation.view.splash.SplashScreen
import com.jooheon.clean_architecture.presentation.view.temp.TestScreen

@OptIn(ExperimentalPermissionsApi::class)
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
                HomeScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Wiki.route) {
                WikipediaScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Map.route) {
                MapScreen(navigator)
            }
            composable(ScreenNavigation.BottomSheet.Search.route) {
                ExoPlayerScreen()
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
                TestScreen()
            }

            composable(ScreenNavigation.Setting.Theme.route) {
                ThemeScreen(navigator = navController)
            }

            composable(ScreenNavigation.Setting.Equalizer.route) {
                EqualizerScreen(navigator = navController)
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
                RepositoryDetailScreen(
                    githubId = githubId,
                    item = repository
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

            bottomSheet(ScreenNavigation.Music.AodPlayer.route) {
                MusicPlayerScreen(navigator = navController)
            }

            bottomSheet(ScreenNavigation.Music.PlayList.route) {
                PlayListScreen()
            }
        }
    }
}


data class BottomSheetLayoutConfig(
    val sheetBackgroundColor: Color = Color.Transparent
)