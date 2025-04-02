package com.jooheon.toyplayer.features.settings.presentation.equalizer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.CustomTopAppBar
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.EqualizerChipRow
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.EqualizerSliderColumn
import com.jooheon.toyplayer.features.settings.presentation.equalizer.dialog.EqualizerDeleteDialog
import com.jooheon.toyplayer.features.settings.presentation.equalizer.dialog.EqualizerSaveDialog
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerDialogState
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiEvent
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState

@Composable
fun EqualizerScreen(
    onBackClick: () -> Unit,
    viewModel: EqualizerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EqualizerScreenInternal(
        uiState = uiState,
        onEvent = viewModel::dispatch,
        onBackClick = onBackClick,
    )
}

@Composable
private fun EqualizerScreenInternal(
    uiState: EqualizerUiState,
    onEvent: (EqualizerUiEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var dialogState by remember { mutableStateOf(EqualizerDialogState.default) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = UiText.StringResource(Strings.setting_equalizer).asString(),
                onClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { onEvent.invoke(EqualizerUiEvent.OnSettingClick(context)) },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .bounceClick { onEvent.invoke(EqualizerUiEvent.OnSettingClick(context)) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(Strings.title_settings),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    EqualizerChipRow(
                        presetGroups = uiState.presetGroups,
                        selectedPreset = uiState.selectedPreset,
                        onPresetSelected = {
                            val event = EqualizerUiEvent.OnPresetSelected(it)
                            onEvent.invoke(event)
                        },
                        onTypeSelected = {
                            val event = EqualizerUiEvent.OnTypeSelected(it)
                            onEvent.invoke(event)
                        },
                        onSaveOrEditClick = {
                            dialogState = EqualizerDialogState(
                                type = EqualizerDialogState.Type.SaveOrEdit,
                                preset = uiState.selectedPreset
                            )
                        },
                        onDeleteClick = {
                            dialogState = EqualizerDialogState(
                                type = EqualizerDialogState.Type.Delete,
                                preset = uiState.selectedPreset
                            )
                        },
                    )

                    EqualizerSliderColumn(
                        centerFrequencies = uiState.selectedPreset.type.frequencies(),
                        gains = uiState.selectedPreset.gains,
                        onGainsChange = {
                            val event = EqualizerUiEvent.OnGainsChanged(it)
                            onEvent.invoke(event)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                when(dialogState.type) {
                    EqualizerDialogState.Type.Delete -> {
                        EqualizerDeleteDialog(
                            state = true to dialogState.preset,
                            onOkButtonClicked = {
                                onEvent.invoke(EqualizerUiEvent.OnPresetDelete(it))
                                dialogState = EqualizerDialogState.default
                            },
                            onDismissRequest = {
                                dialogState = EqualizerDialogState.default
                            }
                        )
                    }
                    EqualizerDialogState.Type.SaveOrEdit -> {
                        EqualizerSaveDialog(
                            state = true to dialogState.preset,
                            onOkButtonClicked = {
                                val event = if(dialogState.preset.isCustomPreset()) EqualizerUiEvent.OnPresetSave(it)
                                else EqualizerUiEvent.OnPresetUpdate(it)
                                onEvent.invoke(event)
                                dialogState = EqualizerDialogState.default
                            },
                            onDismissRequest = {
                                dialogState = EqualizerDialogState.default
                            }
                        )
                    }

                    EqualizerDialogState.Type.None -> { /** nothing **/ }
                }
            }
        }
    )
}

@Composable
@Preview
private fun PreviewEqualizerScreen() {
    ToyPlayerTheme {
        EqualizerScreenInternal(
            uiState = EqualizerUiState.preview,
            onEvent = {},
            onBackClick = {},
        )
    }
}