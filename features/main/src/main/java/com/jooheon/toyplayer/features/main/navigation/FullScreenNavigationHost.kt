package com.jooheon.toyplayer.features.main.navigation

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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.jooheon.toyplayer.features.main.presentation.MainScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.MusicAlbumDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.MusicArtistDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.MusicPlayingQueueScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail.MusicPlaylistDetailScreen
import com.jooheon.toyplayer.features.setting.presentation.equalizer.EqualizerScreen
import com.jooheon.toyplayer.features.setting.presentation.language.LanguageScreen
import com.jooheon.toyplayer.features.setting.presentation.main.SettingScreen
import com.jooheon.toyplayer.features.setting.presentation.theme.ThemeScreen
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail.MusicListDetailScreen
import com.jooheon.toyplayer.features.splash.SplashScreen


@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialNavigationApi::class
)
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

            composable(
                route = ScreenNavigation.Splash.route
            ) {
                SplashScreen(navController)
            }

            composable(
                route = ScreenNavigation.Main.route
            ) {
                MainScreen(navController)
            }
            composable(
                route = ScreenNavigation.Setting.Main.route
            ) {
                SettingScreen(
                    navController = navController,
                    backStackEntry = it,
                )
            }

            composable(
                route = ScreenNavigation.Setting.Language.route
            ) {
                LanguageScreen(
                    navController = navController,
                    backStackEntry = it,
                )
            }

            composable(
                route = ScreenNavigation.Setting.Theme.route
            ) {
                ThemeScreen(
                    navController = navController,
                    backStackEntry = it,
                )
            }

            composable(
                route = ScreenNavigation.Setting.Equalizer.route
            ) {
                EqualizerScreen(navigator = navController)
            }
            composable(
                route = ScreenNavigation.Music.ArtistDetail.route,
                arguments = ScreenNavigation.Music.ArtistDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val artist = ScreenNavigation.Music.ArtistDetail.parseArtist(arguments)

                MusicArtistDetailScreen(
                    navController = navController,
                    artist = artist
                )
            }

            composable(
                route = ScreenNavigation.Music.AlbumDetail.route,
                arguments = ScreenNavigation.Music.AlbumDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val album = ScreenNavigation.Music.AlbumDetail.parseAlbum(arguments)

                MusicAlbumDetailScreen(
                    navController = navController,
                    album = album
                )
            }

            composable(
                route = ScreenNavigation.Music.PlayingQueue.route
            ) {
                MusicPlayingQueueScreen(navController)
            }

            composable(
                route = ScreenNavigation.Music.PlaylistDetail.route,
                arguments = ScreenNavigation.Music.PlaylistDetail.arguments
            ) {
                val arguments = requireNotNull(it.arguments)
                val playlist = ScreenNavigation.Music.PlaylistDetail.parsePlaylist(arguments)

                MusicPlaylistDetailScreen(
                    navController = navController,
                    playlist = playlist
                )
            }

            composable(ScreenNavigation.Music.MusicListDetail.route) {
                val arguments = requireNotNull(it.arguments)
                val musicListType = ScreenNavigation.Music.MusicListDetail.parseType(arguments)

                MusicListDetailScreen(
                    navController = navController,
                    musicListType = musicListType
                )
            }
        }
    }
}