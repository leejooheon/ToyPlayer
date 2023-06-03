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
import com.jooheon.clean_architecture.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.clean_architecture.features.github.main.presentation.main.GithubScreen
import com.jooheon.clean_architecture.features.github.main.presentation.detail.GithubDetailScreen
import com.jooheon.clean_architecture.features.github.main.presentation.detail.GithubDetailScreenViewModel
import com.jooheon.clean_architecture.features.github.main.model.GithubScreenEvent
import com.jooheon.clean_architecture.features.github.main.presentation.main.GithubScreenViewModel
import com.jooheon.clean_architecture.features.main.MainScreen
import com.jooheon.clean_architecture.features.main.MainViewModel
import com.jooheon.clean_architecture.features.map.presentation.MapScreen
import com.jooheon.clean_architecture.features.map.presentation.MapViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.MusicTabPagerScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.MusicAlbumScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.MusicAlbumDetailScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.MusicAlbumDetailScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.MusicArtistScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.MusicArtistDetailScreen
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.MusicArtistDetailScreenViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.MusicPlayerScreenViewModel
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
import com.jooheon.clean_architecture.features.wikipedia.presentation.WikipediaScreen
import com.jooheon.clean_architecture.features.wikipedia.presentation.WikipediaDatailScreen
import com.jooheon.clean_architecture.features.setting.presentation.main.SettingScreen
import com.jooheon.clean_architecture.features.setting.presentation.equalizer.EqualizerScreen
import com.jooheon.clean_architecture.features.setting.presentation.language.LanguageScreen
import com.jooheon.clean_architecture.features.setting.presentation.SettingViewModel
import com.jooheon.clean_architecture.features.setting.presentation.theme.ThemeScreen
import com.jooheon.clean_architecture.features.splash.SplashScreen
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
            composable(ScreenNavigation.BottomSheet.Search.route) {
                val musicPlayerScreenViewModel = hiltViewModel<MusicPlayerScreenViewModel>()
                val musicPlayerScreenState by musicPlayerScreenViewModel.musicPlayerScreenState.collectAsStateWithLifecycle()

                val musicAlbumScreenViewModel = hiltViewModel<MusicAlbumScreenViewModel>().apply {
                    navigateToDetailScreen.observeWithLifecycle {
                        navigator.navigate(ScreenNavigation.Music.AlbumDetail.createRoute(it))
                    }
                }
                val musicAlbumScreenState by musicAlbumScreenViewModel.musicAlbumScreenState.collectAsStateWithLifecycle()

                val musicArtistScreenViewModel = hiltViewModel<MusicArtistScreenViewModel>().apply {
                    navigateToDetailScreen.observeWithLifecycle {
                        navigator.navigate(ScreenNavigation.Music.ArtistDetail.createRoute(it))
                    }
                }
                val musicArtistScreenState by musicArtistScreenViewModel.musicArtistScreenState.collectAsStateWithLifecycle()

                MusicTabPagerScreen(
                    musicPlayerScreenState = musicPlayerScreenState,
                    musicAlbumScreenState = musicAlbumScreenState,
                    musicArtistScreenState = musicArtistScreenState,

                    onMusicPlayerScreenEvent = musicPlayerScreenViewModel::dispatch,
                    onMusicAlbumScreenEvent = musicAlbumScreenViewModel::dispatch,
                    onMusicArtistScreenEvent = musicArtistScreenViewModel::dispatch
                )
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
                val viewModel = hiltViewModel<MainViewModel>().apply {
                    navigateToSettingScreen.observeWithLifecycle {
                        navController.navigate(ScreenNavigation.Setting.Main.route)
                    }
                }
                val state by viewModel.mainScreenState.collectAsStateWithLifecycle()

                MainScreen(
                    navigator = navController,
                    state = state,
                    onEvent = viewModel::dispatch
                )
            }
            composable(ScreenNavigation.Setting.Main.route) {
                val settingViewModel = it.sharedViewModel<SettingViewModel>(navController).apply {
                    navigateTo.observeWithLifecycle {
                        SettingScreenEvent.navigateTo(navController, it)
                    }
                }
                val state by settingViewModel.sharedState.collectAsStateWithLifecycle()

                SettingScreen(
                    state = state,
                    onEvent = settingViewModel::dispatch
                )
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
            composable(ScreenNavigation.Setting.Language .route) {
                val settingViewModel = it.sharedViewModel<SettingViewModel>(
                    navController = navController,
                    parentRoute = ScreenNavigation.Setting.Main.route,
                ).apply {
                    navigateTo.observeWithLifecycle {
                        SettingScreenEvent.navigateTo(navController, it)
                    }
                }
                val state by settingViewModel.sharedState.collectAsStateWithLifecycle()

                LanguageScreen(
                    state = state,
                    onEvent = settingViewModel::dispatch
                )
            }

            composable(ScreenNavigation.Setting.Theme.route) {
                val settingViewModel = it.sharedViewModel<SettingViewModel>(
                    navController = navController,
                    parentRoute = ScreenNavigation.Setting.Main.route,
                ).apply {
                    navigateTo.observeWithLifecycle {
                        SettingScreenEvent.navigateTo(navController, it)
                    }
                }
                val state by settingViewModel.sharedState.collectAsStateWithLifecycle()

                ThemeScreen(
                    state = state,
                    onEvent = settingViewModel::dispatch
                )
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

            composable(
                route = ScreenNavigation.Music.ArtistDetail.route,
                arguments = ScreenNavigation.Music.ArtistDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val artist = ScreenNavigation.Music.ArtistDetail.parseArtist(arguments)

                val musicPlayerScreenViewModel = hiltViewModel<MusicPlayerScreenViewModel>()
                val musicPlayerScreenState by musicPlayerScreenViewModel.musicPlayerScreenState.collectAsStateWithLifecycle()

                val viewModel = hiltViewModel<MusicArtistDetailScreenViewModel>().apply {
                    init(artist)
                    navigateTo.observeWithLifecycle { route ->
                        if(route == ScreenNavigation.Back.route) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(route)
                        }
                    }
                }
                val state by viewModel.musicArtistDetailScreenState.collectAsStateWithLifecycle()

                MusicArtistDetailScreen(
                    musicArtistDetailScreenState = state,
                    musicPlayerScreenState = musicPlayerScreenState,
                    onMusicArtistDetailScreenEvent = viewModel::dispatch,
                    onMusicPlayerScreenEvent = musicPlayerScreenViewModel::dispatch,
                )
            }

            composable(
                route = ScreenNavigation.Music.AlbumDetail.route,
                arguments = ScreenNavigation.Music.AlbumDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val album = ScreenNavigation.Music.AlbumDetail.parseAlbum(arguments)

                val musicPlayerScreenViewModel = hiltViewModel<MusicPlayerScreenViewModel>()
                val musicPlayerScreenState by musicPlayerScreenViewModel.musicPlayerScreenState.collectAsStateWithLifecycle()

                val viewModel = hiltViewModel<MusicAlbumDetailScreenViewModel>().apply {
                    init(album)
                    navigateTo.observeWithLifecycle { route ->
                        if(route == ScreenNavigation.Back.route) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(route)
                        }
                    }
                }
                val state by viewModel.musicAlbumDetailScreenState.collectAsStateWithLifecycle()

                MusicAlbumDetailScreen(
                    musicAlbumDetailScreenState = state,
                    musicPlayerScreenState = musicPlayerScreenState,
                    onMusicAlbumDetailScreenEvent = viewModel::dispatch,
                    onMusicPlayerScreenEvent = musicPlayerScreenViewModel::dispatch,
                )
            }
        }
    }
}


data class BottomSheetLayoutConfig(
    val sheetBackgroundColor: Color = Color.Transparent
)