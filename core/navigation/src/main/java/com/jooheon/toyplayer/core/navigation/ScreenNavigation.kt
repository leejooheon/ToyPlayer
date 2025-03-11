package com.jooheon.toyplayer.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenNavigation {
    fun ScreenNavigation.route() = this.javaClass.name.replace("$", ".")

    @Serializable
    data object Player : ScreenNavigation

    @Serializable
    data object Library : ScreenNavigation

    @Serializable
    data object Back : ScreenNavigation

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
    sealed interface Setting : ScreenNavigation {
        @Serializable
        data object Main : Setting
        @Serializable
        data object Language : Setting
        @Serializable
        data object Theme : Setting
        @Serializable
        data object Equalizer : Setting
    }
}
