package com.jooheon.toyplayer.features.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.jooheon.toyplayer.features.common.R
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.essential.base.UiText

data class BottomScreenNavigationItem(
    val screen: ScreenNavigation.Bottom,
    val label: UiText,
    val contentDescription: UiText,
    val iconImageVector: ImageVector,
    val selectedImageVector: ImageVector
) {
    companion object {
        val items = listOf(
            BottomScreenNavigationItem(
                screen = ScreenNavigation.Bottom.Song,
                label = UiText.StringResource(R.string.title_song),
                contentDescription = UiText.StringResource(R.string.title_cd_song),
                iconImageVector = Icons.Outlined.MusicNote,
                selectedImageVector = Icons.Default.MusicNote,
            ),
            BottomScreenNavigationItem(
                screen = ScreenNavigation.Bottom.Album,
                label = UiText.StringResource(R.string.title_album),
                contentDescription = UiText.StringResource(R.string.title_cd_album),
                iconImageVector = Icons.Outlined.Album,
                selectedImageVector = Icons.Default.Album,
            ),
            BottomScreenNavigationItem(
                screen = ScreenNavigation.Bottom.Artist,
                label = UiText.StringResource(R.string.title_artist),
                contentDescription = UiText.StringResource(R.string.title_cd_artist),
                iconImageVector = Icons.Outlined.Person,
                selectedImageVector = Icons.Default.Person,
            ),
            BottomScreenNavigationItem(
                screen = ScreenNavigation.Bottom.Cache,
                label = UiText.StringResource(R.string.title_cache),
                contentDescription = UiText.StringResource(R.string.title_cd_cache),
                iconImageVector = Icons.Outlined.Cached,
                selectedImageVector = Icons.Default.Cached,
            ),
            BottomScreenNavigationItem(
                screen = ScreenNavigation.Bottom.Playlist,
                label = UiText.StringResource(R.string.title_playlist),
                contentDescription = UiText.StringResource(R.string.title_cd_playlist),
                iconImageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
                selectedImageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
            ),
        )
    }
}