package com.jooheon.toyplayer.features.common.compose

import android.os.Bundle
import androidx.navigation.NavType
import com.jooheon.toyplayer.domain.entity.music.Artist
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }
}