package com.jooheon.toyplayer.features.setting.presentation.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.jooheon.toyplayer.domain.entity.Entity
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation.Bottom.Album.route
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.common.compose.theme.themes.getColorScheme
import com.jooheon.toyplayer.features.essential.base.UiText
import com.jooheon.toyplayer.features.setting.presentation.main.SettingDetailItem
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import com.jooheon.toyplayer.features.setting.model.SettingScreenState
import com.jooheon.toyplayer.features.setting.presentation.SettingViewModel
import com.jooheon.toyplayer.features.common.compose.observeWithLifecycle
import com.jooheon.toyplayer.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.toyplayer.features.common.extension.sharedViewModel
import com.jooheon.toyplayer.features.setting.R

@Composable
fun ThemeScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
) {
    val settingViewModel = backStackEntry.sharedViewModel<SettingViewModel>(
        navController = navController,
        parentRoute = ScreenNavigation.Setting.Main.route()
    ).apply {
        navigateTo.observeWithLifecycle {
            SettingScreenEvent.navigateTo(navController, it)
        }
    }
    val state by settingViewModel.sharedState.collectAsStateWithLifecycle()

    ThemeScreen(
        state = state,
        onEvent = settingViewModel::dispatch
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeScreen(
    state: SettingScreenState,
    onEvent: (Context, SettingScreenEvent) -> Unit
) {
    val context = LocalContext.current
    val supportThemes = Entity.SupportThemes.entries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = UiText.StringResource(R.string.setting_theme).asString(),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            navigationIcon = {
                IconButton(onClick = { onEvent(context, SettingScreenEvent.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        val context = LocalContext.current
        supportThemes.forEach {
            if(SettingScreenState.showableTheme(it)) {
                val selected = it == state.theme
                SettingDetailItem(
                    color = getColorScheme(it).primary,
                    selected = selected,
                    title = it.parse(context),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(context, SettingScreenEvent.OnThemeChanged(it)) }
                )
            }
        }
    }
}

private fun Entity.SupportThemes.parse(context: Context): String {
    val resId = when(this) {
        Entity.SupportThemes.AUTO -> R.string.setting_follow_system
        Entity.SupportThemes.LIGHT -> R.string.setting_theme_light
        Entity.SupportThemes.DARK -> R.string.setting_theme_dark
        Entity.SupportThemes.DYNAMIC_LIGHT -> R.string.setting_theme_dynamic_light
        Entity.SupportThemes.DYNAMIC_DARK -> R.string.setting_theme_dynamic_dark
    }

    return UiText.StringResource(resId).asString(context)
}

@Preview
@Composable
private fun PreviewThemeScreen() {
    PreviewTheme(false) {
        ThemeScreen(
            state = SettingScreenState.default,
            onEvent = { _, _, -> }
        )
    }
}