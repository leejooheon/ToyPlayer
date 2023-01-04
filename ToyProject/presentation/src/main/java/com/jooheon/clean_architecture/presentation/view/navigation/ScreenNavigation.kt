package com.jooheon.clean_architecture.presentation.view.navigation

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


sealed class ScreenNavigation(open val route: String) {

    object Splash: ScreenNavigation("splash")
    object Main: ScreenNavigation("main")


    class Music {
        object AodPlayer: ScreenNavigation("aod_player")
        object PlayList: ScreenNavigation("playlist")
    }

    class Setting {
        object Main: ScreenNavigation("setting_main")
        object Launguage: ScreenNavigation("setting_language")
        object Theme: ScreenNavigation("setting_theme")
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

    class BottomSheet {
        object Github : ScreenNavigation("github")
        object Wiki : ScreenNavigation("wiki")
        object Map : ScreenNavigation("map")
        object Search : ScreenNavigation("search")
        companion object {
            val items = listOf(
                BottomNavigationItem.ImageVectorIcon(
                    screen = ScreenNavigation.BottomSheet.Github,
                    labelResId = R.string.title_github,
                    contentDescriptionResId = R.string.title_cd_github,
                    iconImageVector = Icons.Outlined.Category ,
                    selectedImageVector = Icons.Default.Category,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = ScreenNavigation.BottomSheet.Wiki,
                    labelResId = R.string.title_wikipedia,
                    contentDescriptionResId = R.string.title_cd_wikipedia,
                    iconImageVector = Icons.Outlined.FavoriteBorder,
                    selectedImageVector = Icons.Default.Favorite,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = ScreenNavigation.BottomSheet.Map,
                    labelResId = R.string.title_map,
                    contentDescriptionResId = R.string.title_cd_map,
                    iconImageVector = Icons.Outlined.Map,
                    selectedImageVector = Icons.Default.Map,
                ),
                BottomNavigationItem.ImageVectorIcon(
                    screen = ScreenNavigation.BottomSheet.Search,
                    labelResId = R.string.title_search,
                    contentDescriptionResId = R.string.title_cd_search,
                    iconImageVector = Icons.Outlined.Build,
                    selectedImageVector = Icons.Default.Build,
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