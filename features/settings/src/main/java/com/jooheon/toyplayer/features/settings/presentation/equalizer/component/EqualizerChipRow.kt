package com.jooheon.toyplayer.features.settings.presentation.equalizer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.features.settings.presentation.equalizer.model.EqualizerUiState.PresetGroup
import com.jooheon.toyplayer.features.settings.presentation.equalizer.ext.label

@Composable
internal fun EqualizerChipRow(
    presetGroups: List<PresetGroup>,
    selectedPreset: Preset,
    onTypeSelected: (EqualizerType) -> Unit,
    onPresetSelected: (Preset) -> Unit,
    onSaveOrEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val isPreview = LocalInspectionMode.current
    val selectedGroup = presetGroups
        .find { it.type == selectedPreset.type }
        ?: presetGroups.firstOrNull().default(PresetGroup.default)

    val presets = selectedGroup.presets
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ChipWithDropdown(
                label = selectedGroup.type.toString(),
                options = presetGroups.map {
                    it.type.ordinal to it.type.toString()
                },
                onOptionSelected = { ordinal ->
                    val type = EqualizerType.entries[ordinal]
                    onTypeSelected.invoke(type)
                }
            )
        }

        item {
            ChipWithDropdown(
                label = selectedPreset.label().asString(),
                options = presets.map { it.id to it.label().asString() },
                onOptionSelected = { id ->
                    presets.find { it.id == id }?.let {
                        onPresetSelected.invoke(it)
                    }
                }
            )
        }
        if(selectedPreset.isCustomPreset() || isPreview) {
            item {
                IconButton(
                    onClick = onSaveOrEditClick
                ) {
                    Icon(
                        imageVector = Icons.Default.SaveAlt,
                        contentDescription = UiText.StringResource(Strings.save).asString(),
                    )
                }
            }
        }

        if(selectedPreset.isSavedPreset() || isPreview) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onSaveOrEditClick
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = UiText.StringResource(Strings.edit).asString(),
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = UiText.StringResource(Strings.save).asString(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewEqualizerSlider() {
    ToyPlayerTheme {
        EqualizerChipRow(
            presetGroups = listOf(PresetGroup.preview),
            selectedPreset = Preset.preview,
            onPresetSelected = {},
            onTypeSelected = {},
            onSaveOrEditClick = {},
            onDeleteClick = {},
        )
    }
}