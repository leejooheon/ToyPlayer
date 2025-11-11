package com.jooheon.toyplayer.features.playlist.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.playlist.details.PlaylistDetailScreen
import com.jooheon.toyplayer.features.playlist.main.PlaylistScreen

class PlaylistEntryProviderInstaller(
    private val navigator: Navigator,
): EntryProviderInstaller {
    override operator fun invoke(
        builder: EntryProviderScope<ScreenNavigation>
    ) = with(builder) {
        entry<ScreenNavigation.Playlist.Main> {
            PlaylistScreen(
                navigateTo = navigator::navigateTo,
                onBack = navigator::popBackStack
            )
        }
        entry<ScreenNavigation.Playlist.Details> {
            PlaylistDetailScreen(
                onBack = navigator::popBackStack,
                playlistId = it.playlistId,
            )
        }
    }
}