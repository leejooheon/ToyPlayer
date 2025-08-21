package com.jooheon.toyplayer.features.artist.navigation

import androidx.navigation3.runtime.NavEntry
import com.jooheon.toyplayer.core.navigation.NavMapper
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.artist.details.ArtistDetailScreen
import com.jooheon.toyplayer.features.artist.more.ArtistMoreScreen

fun artistNavGraph(
    navigateTo: (ScreenNavigation) -> Unit,
    onBack: () -> Unit,
): NavMapper = { key ->
    when (key) {
        is ScreenNavigation.Artist.More -> NavEntry(key) {
            ArtistMoreScreen(
                navigateTo = navigateTo,
                onBack = onBack,

            )
        }
        is ScreenNavigation.Artist.Details -> NavEntry(key) {
            ArtistDetailScreen(
                navigateTo = navigateTo,
                onBack = onBack,
                artistId = key.artistId,
            )
        }
        else -> null
    }
}