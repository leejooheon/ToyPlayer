package com.jooheon.toyplayer.features.settings.presentation.equalizer.component.equalizer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.equalizer.component.EqualizerChipRow
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.equalizer.component.EqualizerSliderColumn
import com.jooheon.toyplayer.features.settings.presentation.equalizer.dialog.EqualizerDeleteDialog
import com.jooheon.toyplayer.features.settings.presentation.equalizer.dialog.EqualizerSaveDialog
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerDialogState
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiEvent
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState.PresetGroup

@Composable
internal fun EqualizerSection(
    presetGroups: List<PresetGroup>,
    selectedPreset: Preset,
    onEvent: (EqualizerUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogState by remember { mutableStateOf(EqualizerDialogState.default) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        EqualizerChipRow(
            presetGroups = presetGroups,
            selectedPreset = selectedPreset,
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
                    preset = selectedPreset
                )
            },
            onDeleteClick = {
                dialogState = EqualizerDialogState(
                    type = EqualizerDialogState.Type.Delete,
                    preset = selectedPreset
                )
            },
        )

        EqualizerSliderColumn(
            centerFrequencies = selectedPreset.type.frequencies(),
            gains = selectedPreset.gains,
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

@Composable
@Preview
private fun PreviewEqualizerSection() {
    val uiState = EqualizerUiState.preview
    ToyPlayerTheme {
        EqualizerSection(
            presetGroups = uiState.presetGroups,
            selectedPreset = uiState.selectedPreset,
            onEvent = {},
        )
    }
}