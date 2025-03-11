package com.jooheon.toyplayer.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenNavigation {
    fun ScreenNavigation.route() = this.javaClass.name.replace("$", ".")

    @Serializable
    sealed interface Main : ScreenNavigation {
        @Serializable
        data object Song : Main
        @Serializable
        data object Album : Main
        @Serializable
        data object Artist : Main
        @Serializable
        data object Cache : Main
        @Serializable
        data object Playlist : Main
        @Serializable
        data object Library : Main
    }

    @Serializable
    sealed interface Artist : ScreenNavigation {
        @Serializable
        data object More : Artist
        @Serializable
        data class Details(val artistId: String) : Artist
    }

    @Serializable
    data object Back : ScreenNavigation
    @Serializable
    data object Splash : ScreenNavigation
//    @Serializable
//    data object Main : ScreenNavigation

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
