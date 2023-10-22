package com.jooheon.clean_architecture.features.setting.presentation.language

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
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.setting.presentation.main.SettingDetailItem
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
import com.jooheon.clean_architecture.features.setting.model.SettingScreenState
import com.jooheon.clean_architecture.features.setting.presentation.SettingViewModel
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.toyproject.features.common.compose.observeWithLifecycle
import com.jooheon.clean_architecture.toyproject.features.common.extension.collectAsStateWithLifecycle
import com.jooheon.clean_architecture.toyproject.features.common.extension.sharedViewModel
import com.jooheon.clean_architecture.toyproject.features.setting.R

@Composable
fun LanguageScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
) {
    val settingViewModel = backStackEntry.sharedViewModel<SettingViewModel>(
        navController = navController,
        parentRoute = ScreenNavigation.Setting.Main.route,
    ).apply {
        navigateTo.observeWithLifecycle {
            SettingScreenEvent.navigateTo(navController, it)
        }
    }
    val state by settingViewModel.sharedState.collectAsStateWithLifecycle()

    LanguageScreen(
        state = state,
        onEvent = settingViewModel::dispatch
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageScreen(
    state: SettingScreenState,
    onEvent: (Context, SettingScreenEvent) -> Unit
) {
    val context = LocalContext.current
//    val localizeState = viewModel.localizedState.collectAsState()
    val supportLanguages = Entity.SupportLaunguages.values()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = UiText.StringResource(R.string.setting_language).asString(),
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
        supportLanguages.forEach {
            val selected = it == state.language
            SettingDetailItem(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.background
                },
                selected = selected,
                title = it.parse(context),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(context, SettingScreenEvent.OnLanguageChanged(it)) }
            )
        }
    }
}

fun Entity.SupportLaunguages.parse(context: Context): String {
    val resId = when(this) {
        Entity.SupportLaunguages.AUTO -> R.string.setting_follow_system
        Entity.SupportLaunguages.ENGLISH -> R.string.setting_english
        Entity.SupportLaunguages.KOREAN -> R.string.setting_korean
    }
    return UiText.StringResource(resId).asString(context)
}
@Preview
@Composable
private fun PreviewLaunguageScreen() {

    PreviewTheme(false) {
        LanguageScreen(
            state = SettingScreenState.default,
            onEvent = { _, _ -> }
        )
    }
}