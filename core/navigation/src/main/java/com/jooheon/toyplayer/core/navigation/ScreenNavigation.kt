package com.jooheon.toyplayer.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenNavigation {
    fun ScreenNavigation.route() = this.javaClass.name.replace("$", ".")

    @Serializable
    sealed interface Bottom : ScreenNavigation {
        @Serializable
        data object Song : Bottom
        @Serializable
        data object Album : Bottom
        @Serializable
        data object Artist : Bottom
        @Serializable
        data object Cache : Bottom
        @Serializable
        data object Playlist : Bottom
    }

    @Serializable
    data object Back : ScreenNavigation
    @Serializable
    data object Splash : ScreenNavigation
    @Serializable
    data object Main : ScreenNavigation

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

    @Serializable
    sealed interface Music : ScreenNavigation {
        @Serializable
        data object PlayingQueue : Music
        @Serializable
        data class ArtistDetail(val artistId: String) : Music
        @Serializable
        data class AlbumDetail(val albumId: String) : Music
        @Serializable
        data class PlaylistDetail(val playlistId: Int) : Music
        @Serializable
        data class MusicListDetail(val ordinal: Int) : Music
    }
}
