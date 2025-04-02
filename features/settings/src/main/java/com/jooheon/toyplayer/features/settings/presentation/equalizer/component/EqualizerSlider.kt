package com.jooheon.toyplayer.features.settings.presentation.equalizer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EqualizerSlider(
    frequency: Float,
    gain: Float,
    onGainChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val valueRange = -15f..15f
    var sliderValue by remember { mutableFloatStateOf(gain) }

    LaunchedEffect(gain) {
        sliderValue = gain
    }

    LaunchedEffect(sliderValue){
        delay(100.milliseconds)
        onGainChange(sliderValue)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = frequency.frequencyToString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.15f)
        )

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = valueRange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
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
                            .fillMaxWidth((sliderState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
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

        Text(
            text = "%.1f".format(sliderValue),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.15f),
        )
    }
}

private fun Float.frequencyToString(): String {
    return if (this < 1000f) {
        this.toInt().toString()
    } else {
        val kHz = this / 1000f
        if (kHz % 1f == 0f) "${kHz.toInt()}k" else "${"%.1f".format(kHz)}k"
    }
}

@Composable
@Preview
private fun PreviewEqualizerSlider() {
    ToyPlayerTheme {
        EqualizerSlider(
            frequency = 1000f,
            gain = 0f,
            onGainChange = {},
            modifier = Modifier,
        )
    }
}