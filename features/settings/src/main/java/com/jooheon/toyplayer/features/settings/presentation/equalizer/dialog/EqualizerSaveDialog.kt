package com.jooheon.toyplayer.features.settings.presentation.equalizer.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogButton
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogColumn

@Composable
internal fun EqualizerSaveDialog(
    state: Pair<Boolean, Preset>,
    onOkButtonClicked: (Preset) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val (openDialog, preset) = state
    if(!openDialog) return

    var presetName by remember { mutableStateOf(preset.name.trim()) }
    val title = if(preset.isCustomPreset()) {
        UiText.StringResource(Strings.dialog_new_preset).asString()
    } else {
        UiText.StringResource(Strings.dialog_edit_preset_name).asString()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    DialogColumn(
        fraction = 0.7f,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = presetName,
            onValueChange = {
                presetName = it
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            label = {
                Text(
                    text = stringResource(Strings.dialog_new_preset_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                )
            },
            placeholder = {
                Text(
                    text = stringResource(Strings.dialog_new_preset_placeholder),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DialogButton(
                text = stringResource(Strings.ok),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = {
                    val id = if(preset.isCustomPreset()) 0 else preset.id
                    onOkButtonClicked.invoke(
                        preset.copy(
                            id = id,
                            name = presetName
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            DialogButton(
                text = stringResource(Strings.cancel),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = onDismissRequest
            )
        }
    }
}

@Preview
@Composable
private fun EqualizerSaveDialogPreview() {
    ToyPlayerTheme {
        EqualizerSaveDialog(
            state = true to Preset.preview,
            onOkButtonClicked = {},
            onDismissRequest = {},
        )
    }
}