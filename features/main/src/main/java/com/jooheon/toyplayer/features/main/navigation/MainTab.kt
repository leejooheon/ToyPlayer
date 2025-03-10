package com.jooheon.toyplayer.features.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.common.R
import com.jooheon.toyplayer.core.resources.UiText

enum class MainTab(
    val route: ScreenNavigation.Main,
    val label: UiText,
    val contentDescription: UiText,
    val iconImageVector: ImageVector,
) {
    SONG(
        route = ScreenNavigation.Main.Song,
        label = UiText.StringResource(R.string.title_song),
        contentDescription = UiText.StringResource(R.string.title_cd_song),
        iconImageVector = Icons.Outlined.MusicNote,
    ),
    ALBUM(
        route = ScreenNavigation.Main.Album,
        label = UiText.StringResource(R.string.title_album),
        contentDescription = UiText.StringResource(R.string.title_cd_album),
        iconImageVector = Icons.Outlined.Album,
    ),
    ARTIST(
        route = ScreenNavigation.Main.Artist,
        label = UiText.StringResource(R.string.title_artist),
        contentDescription = UiText.StringResource(R.string.title_cd_artist),
        iconImageVector = Icons.Outlined.Person,
    ),
    CACHE(
        route = ScreenNavigation.Main.Cache,
        label = UiText.StringResource(R.string.title_cache),
        contentDescription = UiText.StringResource(R.string.title_cd_cache),
        iconImageVector = Icons.Outlined.Cached,
    ),
    PLAYLIST(
        route = ScreenNavigation.Main.Playlist,
        label = UiText.StringResource(R.string.title_playlist),
        contentDescription = UiText.StringResource(R.string.title_cd_playlist),
        iconImageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
    ),
    ;


    companion object {
        @Composable
        fun find(predicate: @Composable (ScreenNavigation.Main) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (ScreenNavigation) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}