package com.jooheon.toyplayer.features.settings.presentation.main

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.settings.presentation.main.model.SettingsUiEvent
import com.jooheon.toyplayer.features.settings.presentation.main.model.SettingsUiState
import com.jooheon.toyplayer.features.settings.presentation.main.dialog.LanguageDialog
import com.jooheon.toyplayer.features.settings.presentation.main.component.SettingItem
import com.jooheon.toyplayer.features.settings.presentation.main.dialog.VolumeSeekbarDialog

@Composable
fun SettingScreen(
    navigateTo: (ScreenNavigation) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingScreenInternal(
        uiState = uiState,
        onBackClick = { navigateTo.invoke(ScreenNavigation.Back) },
        onEvent = { event ->
            when(event) {
                is SettingsUiEvent.OnNavigateTheme -> navigateTo.invoke(ScreenNavigation.Settings.Theme)
                is SettingsUiEvent.OnNavigateOpenSourceLicense -> context.startActivity(
                    Intent(context, OssLicensesMenuActivity::class.java)
                )
                else -> viewModel.dispatch(context, event)
            }
        },
    )
}

@Composable
private fun SettingScreenInternal(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    var dialogState by remember { mutableStateOf(SettingsUiState.DialogState.NONE) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = UiText.StringResource(Strings.title_settings).asString(),
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(uiState.models) {
                        SettingItem(
                            model = it,
                            onClick = {
                                when(it.event) {
                                    SettingsUiEvent.OnLanguageDialog -> { dialogState = SettingsUiState.DialogState.LANGUAGE }
                                    SettingsUiEvent.OnVolumeDialog -> { dialogState = SettingsUiState.DialogState.VOLUME }
                                    else -> onEvent.invoke(it.event)
                                }
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                fun resetDialog() { dialogState = SettingsUiState.DialogState.NONE }
                when(dialogState) {
                    SettingsUiState.DialogState.LANGUAGE -> {
                        LanguageDialog(
                            fraction = 0.7f,
                            currentLanguageType = uiState.currentLanguageType,
                            onDismissRequest = { resetDialog() },
                            onSelected = {
                                onEvent.invoke(SettingsUiEvent.OnLanguageSelected(it))
                                resetDialog()
                            },
                        )
                    }
                    SettingsUiState.DialogState.VOLUME -> {
                        VolumeSeekbarDialog(
                            fraction = 0.7f,
                            volume = uiState.volume,
                            onDismissRequest = { resetDialog() },
                            onVolumeChanged = { onEvent.invoke(SettingsUiEvent.OnVolumeChanged(it)) },
                            onApply = {
                                onEvent.invoke(SettingsUiEvent.OnVolumeChanged(it))
                                resetDialog()
                            }
                        )
                    }
                    SettingsUiState.DialogState.NONE -> { /** nothing **/ }
                }
            }
        }
    )
}

@Composable
@Preview
private fun PreviewSettingScreen() {
    ToyPlayerTheme {
        SettingScreenInternal(
            uiState = SettingsUiState.default,
            onEvent = {},
            onBackClick = {},
        )
    }
}