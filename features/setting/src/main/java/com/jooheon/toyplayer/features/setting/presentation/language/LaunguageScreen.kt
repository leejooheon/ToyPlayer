package com.jooheon.toyplayer.features.setting.presentation.language

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
import com.jooheon.toyplayer.core.strings.UiText
import com.jooheon.toyplayer.domain.entity.SupportThemes
import com.jooheon.toyplayer.features.setting.R
import com.jooheon.toyplayer.features.setting.model.SettingScreenEvent
import com.jooheon.toyplayer.features.setting.model.SettingScreenState
import com.jooheon.toyplayer.features.setting.presentation.main.SettingDetailItem

@Composable
fun LanguageScreen(
) {
    LanguageScreen(
        state = SettingScreenState.default,
        onEvent = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageScreen(
    state: SettingScreenState,
    onEvent: (SettingScreenEvent) -> Unit
) {
    val context = LocalContext.current
//    val localizeState = viewModel.localizedState.collectAsState()
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
                IconButton(onClick = { onEvent(SettingScreenEvent.OnBackClick) }) {
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
        
//        supportLanguages.forEach {
//            val selected = it == state.language
//            SettingDetailItem(
//                color = if (selected) {
//                    MaterialTheme.colorScheme.primary
//                } else {
//                    MaterialTheme.colorScheme.background
//                },
//                selected = selected,
//                title = it.parse(context),
//                modifier = Modifier.fillMaxWidth(),
//                onClick = { onEvent(SettingScreenEvent.OnLanguageChanged(it)) }
//            )
//        }
    }
}

@Preview
@Composable
private fun PreviewLaunguageScreen() {
    ToyPlayerTheme {
        LanguageScreen(
            state = SettingScreenState.default,
            onEvent = {}
        )
    }
}