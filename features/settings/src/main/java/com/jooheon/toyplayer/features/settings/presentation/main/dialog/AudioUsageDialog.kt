package com.jooheon.toyplayer.features.settings.presentation.main.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.ext.bounceClick
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.audio.AudioUsage
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogButton
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogColumn

@Composable
internal fun AudioUsageDialog(
    fraction: Float,
    audioUsage: AudioUsage,
    onDismissRequest: () -> Unit,
    onSelected: (AudioUsage) -> Unit,
) {
    var currentType by remember { mutableStateOf(audioUsage) }

    DialogColumn(
        fraction = fraction,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = UiText.StringResource(Strings.dialog_audio_usage_title).asString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = UiText.StringResource(Strings.dialog_audio_usage_description).asString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        )

        AudioUsage.entries.forEach {
            val resource = when(it) {
                AudioUsage.MEDIA -> Strings.dialog_audio_usage_media_description
                AudioUsage.MIX -> Strings.dialog_audio_usage_mix_description
            }
            val description = UiText.StringResource(resource)
            Spacer(modifier = Modifier.height(8.dp))
            AudioUsageItem(
                type = it,
                description = description,
                currentType = currentType,
                onCheckedClick = {
                    currentType = it
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DialogButton(
                text = stringResource(Strings.ok),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                onClick = { onSelected.invoke(currentType) }
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

@Composable
internal fun AudioUsageItem(
    type: AudioUsage,
    description: UiText,
    currentType: AudioUsage,
    onCheckedClick: (AudioUsage) -> Unit,
) {
    val isChecked = currentType == type

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if(isChecked) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = { onCheckedClick.invoke(type) })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 4.dp,
                    top = 10.dp,
                    bottom = 10.dp
                )
        ) {
            Text(
                text = type.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if(isChecked) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )

            Text(
                text = description.asString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if(isChecked) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAudioContentTypeDialog() {
    ToyPlayerTheme {
        AudioUsageDialog(
            fraction = 0.7f,
            audioUsage = AudioUsage.MEDIA,
            onDismissRequest = {},
            onSelected = {},
        )
    }
}