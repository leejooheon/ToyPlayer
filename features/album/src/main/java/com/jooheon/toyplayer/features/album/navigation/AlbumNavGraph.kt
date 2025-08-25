package com.jooheon.toyplayer.features.album.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.album.details.AlbumDetailScreen
import com.jooheon.toyplayer.features.album.more.AlbumMoreScreen

class AlbumEntryProviderInstaller(
    private val navigator: Navigator,
): EntryProviderInstaller {
    override operator fun invoke(
        builder: EntryProviderBuilder<ScreenNavigation>
    ) = with(builder) {
        entry<ScreenNavigation.Album.More> {
            AlbumMoreScreen(
                onBack = navigator::popBackStack,
            )
        }
        entry<ScreenNavigation.Album.Details> {
            AlbumDetailScreen(
                onBack = navigator::popBackStack,
                albumId = it.albumId,
            )
        }
    }
}