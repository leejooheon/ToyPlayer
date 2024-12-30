package com.jooheon.toyplayer.features.musicplayer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.song.MusicSongScreen

fun NavGraphBuilder.songNavGraph(
    navigate: (ScreenNavigation.Music) -> Unit,
) {
    composable<ScreenNavigation.Main.Song> {
        MusicSongScreen(
            navigate = navigate
        )
    }
}
