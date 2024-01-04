package com.jooheon.toyplayer.features.common.compose

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.features.common.R
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Serializable


sealed class ScreenNavigation(open val route: String) {
    data object Back: ScreenNavigation("back")
    data object Splash: ScreenNavigation("splash")
    data object Main: ScreenNavigation("main")

    class Setting {
        data object Main: ScreenNavigation("setting_main")
        data object Language: ScreenNavigation("setting_language")
        data object Theme: ScreenNavigation("setting_theme")
        data object Equalizer: ScreenNavigation("setting_equalizer")
    }

    class Music {
        data object PlayingQueue: ScreenNavigation("music_playing_queue_detail")
        data object ArtistDetail: ScreenNavigation("music_artist_detail?item={artist}") {
            const val artist = "artist"
            val arguments = listOf(
                navArgument(artist) {
                    type = createSerializableNavType<Artist>()
                }
            )
            fun createRoute(artist: Artist): String {
                val item = Json.encodeToString(artist)
                return "music_artist_detail?item=${item}"
            }
            fun parseArtist(bundle: Bundle): Artist {
                val artist = bundle.getSerializable(artist) as Artist
                return artist
            }
        }

        data object AlbumDetail: ScreenNavigation("music_album_detail?item={album}") {
            const val album = "album"
            val arguments = listOf(
                navArgument(album) {
                    type = createSerializableNavType<Album>()
                }
            )
            fun createRoute(album: Album): String {
                val item = Json.encodeToString(album)
                return "music_album_detail?item=${item}"
            }
            fun parseAlbum(bundle: Bundle): Album {
                val artist = bundle.getSerializable(album) as Album
                return artist
            }
        }

        data object PlaylistDetail: ScreenNavigation("music_playlist_detail?item={playlist}") {
            const val playlist = "playlist"
            val arguments = listOf(
                navArgument(playlist) {
                    type = createSerializableNavType<Playlist>()
                }
            )
            fun createRoute(playlist: Playlist): String {
                val item = Json.encodeToString(playlist)
                return "music_playlist_detail?item=${item}"
            }
            fun parsePlaylist(bundle: Bundle): Playlist {
                val playlist = bundle.getSerializable(PlaylistDetail.playlist) as Playlist
                return playlist
            }
        }
    }

    class BottomSheet {
        data object Song : ScreenNavigation("Song")
        data object Album : ScreenNavigation("Album")
        data object Artist : ScreenNavigation("Artist")
        data object Cache : ScreenNavigation("Cache")
        data object Playlist : ScreenNavigation("Playlist")
        companion object {
            val items = listOf(
                BottomNavigationItem.ImageVectorIcon(
                    screen = Song,
                    labelResId = R.string.title_song,
                    contentDescriptionResId = R.string.title_cd_song,
                    iconImageVector = Icons.Outlined.MusicNote,
                    selectedImageVector = Icons.Default.MusicNote,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Album,
                    labelResId = R.string.title_album,
                    contentDescriptionResId = R.string.title_cd_album,
                    iconImageVector = Icons.Outlined.Album,
                    selectedImageVector = Icons.Default.Album,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Artist,
                    labelResId = R.string.title_artist,
                    contentDescriptionResId = R.string.title_cd_artist,
                    iconImageVector = Icons.Outlined.Person,
                    selectedImageVector = Icons.Default.Person,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Cache,
                    labelResId = R.string.title_cache,
                    contentDescriptionResId = R.string.title_cd_cache,
                    iconImageVector = Icons.Outlined.Cached,
                    selectedImageVector = Icons.Default.Cached,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Playlist,
                    labelResId = R.string.title_playlist,
                    contentDescriptionResId = R.string.title_cd_playlist,
                    iconImageVector = Icons.Outlined.PlaylistPlay,
                    selectedImageVector = Icons.Default.PlaylistPlay,
                ),
            )
        }
    }
}

sealed class BottomNavigationItem(
    val screen: ScreenNavigation,
    @StringRes val labelResId: Int,
    @StringRes val contentDescriptionResId: Int,
) {
    class ResourceIcon(
        screen: ScreenNavigation,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        @DrawableRes val iconResId: Int,
        @DrawableRes val selectedIconResId: Int? = null,
    ) : BottomNavigationItem(screen, labelResId, contentDescriptionResId)

    class ImageVectorIcon(
        screen: ScreenNavigation,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        val iconImageVector: ImageVector,
        val selectedImageVector: ImageVector? = null,
    ) : BottomNavigationItem(screen, labelResId, contentDescriptionResId)
}


// https://pluu.github.io/blog/android/2022/02/04/compose-pending-argument-part-2/
inline fun <reified T : Serializable> createSerializableNavType(
    isNullableAllowed: Boolean = false
): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {
        override val name: String
            get() = "SupportSerializable"

        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putSerializable(key, value) // Bundle에 Serializable 타입으로 추가
        }

        override fun get(bundle: Bundle, key: String): T? {
            return bundle.getSerializable(key) as? T // Bundle에서 Serializable 타입으로 꺼낸다
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString(value) // String 전달된 Parsing 방법을 정의
        }
    }
}