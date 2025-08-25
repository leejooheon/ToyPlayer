package com.jooheon.toyplayer.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenNavigation: NavKey {
    fun ScreenNavigation.route() = this.javaClass.name.replace("$", ".")

    @Serializable
    data object Player : ScreenNavigation

    @Serializable
    data object Library : ScreenNavigation

    @Serializable
    data object Splash : ScreenNavigation

    @Serializable
    sealed interface Album : ScreenNavigation {
        @Serializable
        data object More : Album
        @Serializable
        data class Details(val albumId: String) : Album
    }

    @Serializable
    sealed interface Artist : ScreenNavigation {
        @Serializable
        data object More : Artist
        @Serializable
        data class Details(val artistId: String) : Artist
    }

    @Serializable
    sealed interface Playlist : ScreenNavigation {
        @Serializable
        data object Main : Playlist
        @Serializable
        data class Details(val playlistId: Int) : Playlist
    }

    @Serializable
    sealed interface Settings : ScreenNavigation {
        @Serializable
        data object Main : Settings
        @Serializable
        data object Theme : Settings
        @Serializable
        data object Equalizer : Settings
    }
}
