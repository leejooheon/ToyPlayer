package com.jooheon.clean_architecture.features.common.compose

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.features.common.BuildConfig
import com.jooheon.clean_architecture.features.common.R
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Serializable


sealed class ScreenNavigation(open val route: String) {
    object Back: ScreenNavigation("back")
    object Splash: ScreenNavigation("splash")
    object Main: ScreenNavigation("main")
    object Subway: ScreenNavigation("subway") {
        const val WidgetDeeplink = BuildConfig.DEEPLINK_PREFIX + "/subway/widget"
    }

    class Setting {
        object Main: ScreenNavigation("setting_main")
        object Language: ScreenNavigation("setting_language")
        object Theme: ScreenNavigation("setting_theme")
        object Equalizer: ScreenNavigation("setting_equalizer")
    }

    class Detail {
        object GithubDetail: ScreenNavigation(route = "github_detail?id={github_id}&item={repository}") {
            const val repository = "repository"
            const val githubId = "github_id"

            val arguments = listOf(
                navArgument(repository) {
                    type = createSerializableNavType<Entity.Repository>()
                },
                navArgument(githubId) {
                    type = NavType.StringType
                }
            )

            fun createRoute(githubId: String, repository: Entity.Repository): String {
                val id = Json.encodeToString(githubId)
                val item = Json.encodeToString(repository)
                return "github_detail?id=${id}&item=${item}"
            }
            fun parseGithubId(bundle: Bundle): String {
                val githubId = bundle.getString(githubId, "empty")
                return githubId
            }
            fun parseRepository(bundle: Bundle): Entity.Repository {
                val repository = bundle.getSerializable(repository) as Entity.Repository
                return repository
            }
        }
        object WikipediaDetail: ScreenNavigation("wikipedia_detail?item={page}") {
            const val page = "page"
            val arguments = listOf(
                navArgument(page) {
                    type = createSerializableNavType<Entity.Related.Page>()
                }
            )
            fun createRoute(page: Entity.Related.Page): String {
                val item = Json.encodeToString(page)
                return "wikipedia_detail?item=${item}"
            }
            fun parsePage(bundle: Bundle): Entity.Related.Page {
                val page = bundle.getSerializable(page) as Entity.Related.Page
                return page
            }
        }
    }

    class Music {
        object ArtistDetail: ScreenNavigation("music_artist_detail?item={artist}") {
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

        object AlbumDetail: ScreenNavigation("music_album_detail?item={album}") {
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
    }

    class BottomSheet {
        object Github : ScreenNavigation("github")
        object Wiki : ScreenNavigation("wiki")
        object Map : ScreenNavigation("map")
        object Music : ScreenNavigation("music")
        companion object {
            val items = listOf(
                BottomNavigationItem.ImageVectorIcon(
                    screen = Music,
                    labelResId = R.string.title_music,
                    contentDescriptionResId = R.string.title_cd_music,
                    iconImageVector = Icons.Outlined.MusicNote,
                    selectedImageVector = Icons.Default.MusicNote,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Wiki,
                    labelResId = R.string.title_wikipedia,
                    contentDescriptionResId = R.string.title_cd_wikipedia,
                    iconImageVector = Icons.Outlined.FavoriteBorder,
                    selectedImageVector = Icons.Default.Favorite,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Github,
                    labelResId = R.string.title_github,
                    contentDescriptionResId = R.string.title_cd_github,
                    iconImageVector = Icons.Outlined.Category,
                    selectedImageVector = Icons.Default.Category,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = Map,
                    labelResId = R.string.title_map,
                    contentDescriptionResId = R.string.title_cd_map,
                    iconImageVector = Icons.Outlined.Map,
                    selectedImageVector = Icons.Default.Map,
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