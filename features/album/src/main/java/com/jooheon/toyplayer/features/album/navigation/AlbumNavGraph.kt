package com.jooheon.toyplayer.features.album.navigation

import androidx.navigation3.runtime.NavEntry
import com.jooheon.toyplayer.core.navigation.NavMapper
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.album.details.AlbumDetailScreen
import com.jooheon.toyplayer.features.album.more.AlbumMoreScreen

fun albumNavGraph(
    onBack: () -> Unit,
): NavMapper = { key ->
    when (key) {
        is ScreenNavigation.Album.More -> NavEntry(key) {
            AlbumMoreScreen(
                onBack = onBack,
            )
        }
        is ScreenNavigation.Album.Details -> NavEntry(key) {
            AlbumDetailScreen(
                onBack = onBack,
                albumId = key.albumId,
            )
        }
        else -> null
    }
}