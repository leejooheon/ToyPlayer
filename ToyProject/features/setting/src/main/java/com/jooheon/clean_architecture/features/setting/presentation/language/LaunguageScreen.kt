package com.jooheon.clean_architecture.features.setting.presentation.language

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
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.setting.R
import com.jooheon.clean_architecture.features.setting.presentation.main.SettingDetailItem
import com.jooheon.clean_architecture.features.setting.model.SettingScreenEvent
import com.jooheon.clean_architecture.features.setting.model.SettingScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    state: SettingScreenState,
    onEvent: (Context, SettingScreenEvent, SettingScreenState) -> Unit
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
                IconButton(onClick = { onEvent(context, SettingScreenEvent.GoToBack, state) }) {
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
                onClick = { onEvent(context, SettingScreenEvent.LanguageChanged, state.copy(language = it)) }
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
            onEvent = { _, _, _ -> }
        )
    }
}