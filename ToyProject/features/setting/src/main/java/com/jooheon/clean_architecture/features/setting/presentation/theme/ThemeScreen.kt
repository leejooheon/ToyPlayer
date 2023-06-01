package com.jooheon.clean_architecture.features.setting.presentation.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.common.compose.theme.themes.getColorScheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.setting.R
import com.jooheon.clean_architecture.features.setting.presentation.main.SettingDetailItem
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
import com.jooheon.clean_architecture.features.setting.model.SettingScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    state: SettingScreenState,
    onEvent: (Context, SettingScreenEvent) -> Unit
) {
    val context = LocalContext.current
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