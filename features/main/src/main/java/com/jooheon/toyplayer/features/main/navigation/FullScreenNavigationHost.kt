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
import androidx.navigation.toRoute
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.main.presentation.MainScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.MusicAlbumDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.MusicArtistDetailScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.MusicPlayingQueueScreen
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playlist.detail.MusicPlaylistDetailScreen
import com.jooheon.toyplayer.features.setting.presentation.equalizer.EqualizerScreen
import com.jooheon.toyplayer.features.setting.presentation.language.LanguageScreen
import com.jooheon.toyplayer.features.setting.presentation.main.SettingScreen
import com.jooheon.toyplayer.features.setting.presentation.theme.ThemeScreen
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
            startDestination = ScreenNavigation.Splash,
            modifier = Modifier.fillMaxSize()
        ) {

            composable<ScreenNavigation.Splash> {
                SplashScreen(navController)
            }

            composable<ScreenNavigation.Main> {
                MainScreen(navController)
            }
            composable<ScreenNavigation.Setting.Main> {
                SettingScreen(
                    navController = navController,
                    backStackEntry = it,
                )
            }

            composable<ScreenNavigation.Setting.Language> {
                LanguageScreen(
                    navController = navController,
                    backStackEntry = it,
                )
            }

            composable<ScreenNavigation.Setting.Theme> {
                ThemeScreen(
                    navController = navController,
                    backStackEntry = it,
                )
            }

            composable<ScreenNavigation.Setting.Equalizer> {
                EqualizerScreen(navigator = navController)
            }

            composable<ScreenNavigation.Music.PlayingQueue> {
                MusicPlayingQueueScreen(navController)
            }

            composable<ScreenNavigation.Music.ArtistDetail> {
                val args = it.toRoute<ScreenNavigation.Music.ArtistDetail>()
                MusicArtistDetailScreen(
                    navController = navController,
                    artistId = args.artistId
                )
            }

            composable<ScreenNavigation.Music.AlbumDetail> {
                val args = it.toRoute<ScreenNavigation.Music.AlbumDetail>()
                MusicAlbumDetailScreen(
                    navController = navController,
                    albumId = args.albumId
                )
            }

            composable<ScreenNavigation.Music.PlaylistDetail>{
                val args = it.toRoute<ScreenNavigation.Music.PlaylistDetail>()
                MusicPlaylistDetailScreen(
                    navController = navController,
                    playlistId = args.playlistId
                )
            }

            composable<ScreenNavigation.Music.MusicListDetail> {
                val args = it.toRoute<ScreenNavigation.Music.MusicListDetail>()
                MusicListDetailScreen(
                    navController = navController,
                    musicListType = MusicListType.entries[args.ordinal]
                )
            }
        }
    }
}