package com.jooheon.toyplayer.features.playlist.navigation

import androidx.navigation3.runtime.NavEntry
import com.jooheon.toyplayer.core.navigation.NavMapper
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.playlist.details.PlaylistDetailScreen
import com.jooheon.toyplayer.features.playlist.main.PlaylistScreen


fun playlistNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
    onBack: () -> Unit,
): NavMapper = { key ->
    when (key) {
        is ScreenNavigation.Playlist.Main -> NavEntry(key) {
            PlaylistScreen(
                navigateTo = navigateTo,
                onBack = onBack
            )
        }
        is ScreenNavigation.Playlist.Details -> NavEntry(key) {
            PlaylistDetailScreen(
                onBack = onBack,
                playlistId = key.playlistId,
            )
        }
        else -> null
    }
}