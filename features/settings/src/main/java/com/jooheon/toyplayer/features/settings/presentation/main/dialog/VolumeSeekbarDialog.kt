package com.jooheon.toyplayer.features.settings.presentation.main.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderDefaults.Thumb
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogButton
import com.jooheon.toyplayer.features.commonui.components.dialog.DialogColumn
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
internal fun VolumeSeekbarDialog(
    fraction: Float,
    volume: Float,
    onDismissRequest: () -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onApply:(Float) -> Unit,
) {
    val valueRange =  0f..1f
    var currentVolume by remember { mutableFloatStateOf(volume) }

    LaunchedEffect(volume) {
        snapshotFlow { currentVolume }
            .debounce(250)
            .distinctUntilChanged()
            .collectLatest { debouncedVolume ->
                onVolumeChanged(debouncedVolume)
            }
    }

    DialogColumn(
        fraction = fraction,
        padding = 16.dp,
        onDismissRequest = onDismissRequest,
    ) {
        Text(
            text = UiText.StringResource(Strings.dialog_volume_title).asString(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = UiText.StringResource(Strings.dialog_volume_description).asString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        )

        Slider(
            value = currentVolume,
            onValueChange = { currentVolume = it },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            track = { sliderState ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(MaterialTheme.shapes.small)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(sliderState.value / valueRange.endInclusive)
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            },
            thumb = {
                Thumb(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.White, shape = CircleShape),
                    interactionSource = remember { MutableInteractionSource() },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        DialogButton(
            text = stringResource(Strings.apply),
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = { onApply.invoke(currentVolume) }
        )
    }
}

@Preview
@Composable
private fun PreviewVolumeSeekbarDialog() {
    ToyPlayerTheme {
        VolumeSeekbarDialog(
            fraction = 0.7f,
            volume = 0.5f,
            onDismissRequest = {},
            onVolumeChanged = {},
            onApply = {}
        )
    }
}