package com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.UiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
internal fun VolumeSlider(
    modifier: Modifier = Modifier,
    title: UiText,
    volume: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onVolumeChanged: (Float) -> Unit,
    steps: Int = 0,
) {
    var currentVolume by remember { mutableFloatStateOf(volume) }

    LaunchedEffect(Unit) {
        snapshotFlow { currentVolume }
            .debounce(200)
            .distinctUntilChanged()
            .collectLatest { onVolumeChanged(it) }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = title.asString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        )

        Slider(
            value = currentVolume,
            onValueChange = { currentVolume = it },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            steps = steps,
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
    }

}
@Preview
@Composable
private fun PreviewVolumeSlider() {
    ToyPlayerTheme {
        VolumeSlider(
            title = UiText.DynamicString("label"),
            volume = 0.5f,
            valueRange = 0f..1f,
            onVolumeChanged = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .size(300.dp)
                .padding(16.dp)
        )
    }
}