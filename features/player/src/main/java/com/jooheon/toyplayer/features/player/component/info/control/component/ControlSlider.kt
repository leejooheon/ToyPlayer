package com.jooheon.toyplayer.features.player.component.info.control.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.utils.MusicUtil


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ControlSlider(
    duration: Long,
    currentPosition: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var userInput by remember { mutableStateOf<Long?>(null) }
    var isDragging by remember { mutableStateOf(false) }
    val positionToShow = userInput ?: currentPosition
    val valueRange = 0f..duration.toFloat()

    LaunchedEffect(currentPosition) {
        if (!isDragging) userInput = null
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = MusicUtil.toReadableDurationString(currentPosition),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.surface,
            textAlign = TextAlign.Center,
            modifier = Modifier,
        )

        Slider(
            value = positionToShow.toFloat(),
            onValueChange = {
                userInput = it.toLong()
                isDragging = true
            },
            onValueChangeFinished = {
                userInput?.let { onSeek(it) }
                isDragging = false
            },
            valueRange = valueRange,
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
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(sliderState.value / valueRange.endInclusive)
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            },
            thumb = {
                Thumb(
                    modifier = Modifier
                        .size(12.dp)
                        .offset(y = 2.dp)
                        .background(Color.White, shape = CircleShape),
                    interactionSource = remember { MutableInteractionSource() },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            modifier = modifier.weight(1f),
        )

        Text(
            text = MusicUtil.toReadableDurationString(duration - currentPosition),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.surface,
            textAlign = TextAlign.Center,
            modifier = Modifier,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = android.graphics.Color.GRAY.toLong(),
)
@Composable
private fun PreviewControlSlider() {
    ToyPlayerTheme {
        ControlSlider(
            duration = 12000L,
            currentPosition = 5000L,
            onSeek = {},
        )
    }
}