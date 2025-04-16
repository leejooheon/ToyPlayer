package com.jooheon.toyplayer.features.settings.presentation.equalizer.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Preset
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogButton
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogColumn

@Composable
internal fun EqualizerDeleteDialog(
    state: Pair<Boolean, Preset>,
    onOkButtonClicked: (Preset) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val (openDialog, preset) = state
    if(!openDialog) return

    DialogColumn(
        fraction = 0.7f,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = UiText.StringResource(Strings.dialog_preset_delete).asString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
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
                onClick = { onOkButtonClicked.invoke(preset) }
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
private fun EqualizerDeleteDialogPreview() {
    ToyPlayerTheme {
        EqualizerDeleteDialog(
            state = true to Preset.preview,
            onOkButtonClicked = {},
            onDismissRequest = {},
        )
    }
}