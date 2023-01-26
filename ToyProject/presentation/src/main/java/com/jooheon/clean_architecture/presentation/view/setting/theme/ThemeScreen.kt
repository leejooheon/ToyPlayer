package com.jooheon.clean_architecture.presentation.view.setting.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.theme.themes.getColorScheme
import com.jooheon.clean_architecture.presentation.utils.UiText
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.setting.SettingDetailItem
import com.jooheon.clean_architecture.presentation.view.setting.SettingViewModel
import com.jooheon.clean_architecture.presentation.view.temp.EmptySettingUseCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel(sharedViewModel())
) {
    val themeState = viewModel.themeState.collectAsState()
    val supportThemes = Entity.SupportThemes.values()

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
                IconButton(onClick = { navigator.popBackStack() }) {
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
            if(viewModel.showableTheme(it)) {
                val selected = it == themeState.value
                SettingDetailItem(
                    color = getColorScheme(it).primary,
                    selected = selected,
                    title = it.parse(context),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.onThemeItemClick(it) }
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
    val context = LocalContext.current
    PreviewTheme(false) {
        ThemeScreen(
            navigator = NavController(context),
            viewModel = SettingViewModel(EmptySettingUseCase())
        )
    }
}