package com.jooheon.toyplayer.features.setting.presentation.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.SupportThemes
import com.jooheon.toyplayer.features.setting.R
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import com.jooheon.toyplayer.features.setting.model.SettingScreenState
import com.jooheon.toyplayer.features.setting.presentation.main.SettingDetailItem

@Composable
fun ThemeScreen(
    // add viewmodel, state
) {
    ThemeScreen(
        state = SettingScreenState.default,
        onEvent = { _, _ ->

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeScreen(
    state: SettingScreenState,
    onEvent: (Context, SettingScreenEvent) -> Unit
) {
    val context = LocalContext.current
    val supportThemes = SupportThemes.entries

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

        supportThemes.forEach {
            if(SettingScreenState.showableTheme(it)) {
                val selected = it == state.theme
                SettingDetailItem(
                    color =  MaterialTheme.colorScheme.primary,
                    selected = selected,
                    title = it.parse(context),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(context, SettingScreenEvent.OnThemeChanged(it)) }
                )
            }
        }
    }
}

private fun SupportThemes.parse(context: Context): String {
    val resId = when(this) {
        SupportThemes.AUTO -> R.string.setting_follow_system
        SupportThemes.LIGHT -> R.string.setting_theme_light
        SupportThemes.DARK -> R.string.setting_theme_dark
        SupportThemes.DYNAMIC_LIGHT -> R.string.setting_theme_dynamic_light
        SupportThemes.DYNAMIC_DARK -> R.string.setting_theme_dynamic_dark
    }

    return UiText.StringResource(resId).asString(context)
}

@Preview
@Composable
private fun PreviewThemeScreen() {
    ToyPlayerTheme {
        ThemeScreen(
            state = SettingScreenState.default,
            onEvent = { _, _, -> }
        )
    }
}