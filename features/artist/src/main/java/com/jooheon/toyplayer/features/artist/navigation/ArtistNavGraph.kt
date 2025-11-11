package com.jooheon.toyplayer.features.artist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.artist.details.ArtistDetailScreen
import com.jooheon.toyplayer.features.artist.more.ArtistMoreScreen

class ArtistEntryProviderInstaller(
    private val navigator: Navigator,
): EntryProviderInstaller {
    override operator fun invoke(
        builder: EntryProviderScope<ScreenNavigation>
    ) = with(builder) {
        entry<ScreenNavigation.Artist.More> {
            ArtistMoreScreen(
                navigateTo = navigator::navigateTo,
                onBack = navigator::popBackStack,
            )
        }
        entry<ScreenNavigation.Artist.Details> {
            ArtistDetailScreen(
                navigateTo = navigator::navigateTo,
                onBack = navigator::popBackStack,
                artistId = it.artistId,
            )
        }
    }
}