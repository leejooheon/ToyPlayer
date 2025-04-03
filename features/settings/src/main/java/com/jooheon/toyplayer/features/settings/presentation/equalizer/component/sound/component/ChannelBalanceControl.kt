package com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.toDegrees
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.atan2

@Composable
internal fun ChannelBalanceControl(
    title: UiText,
    initialValue: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var angle by remember { mutableFloatStateOf(((initialValue + 1f) / 2f) * 270f) }

    val unFilledColor = MaterialTheme.colorScheme.background
    val filledColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(Unit) {
        snapshotFlow { angle }
            .map { it / 270f }
            .debounce(200)
            .distinctUntilChanged()
            .collectLatest {
                val balance = it * 2f - 1f // ✅ -1.0 ~ 1.0 변환
                onValueChange(balance)
            }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val touch = change.position - center
                        val theta = atan2(touch.y, touch.x).toDegrees()
                        val fixedTheta = (theta + 360f + 135f) % 360f
                        if (fixedTheta <= 270f) {
                            angle = fixedTheta
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 20f

                drawArc(
                    color = unFilledColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = stroke)
                )

                drawArc(
                    color = filledColor,
                    startAngle = 135f,
                    sweepAngle = angle,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }

            val balance = ((angle / 270f) * 2f - 1f).coerceIn(-1f, 1f)

            val leftGain = if (balance <= 0f) 1f else 1f - balance
            val rightGain = if (balance >= 0f) 1f else 1f + balance

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { },
            ) {
                Text(
                    text = "L ${(leftGain * 100).toInt()}% / R ${(rightGain * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = title.asString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview
@Composable
private fun PreviewSoundSection() {
    ToyPlayerTheme {
        ChannelBalanceControl(
            title = UiText.StringResource(Strings.equalizer_audio_channel_balance),
            initialValue = 0f,
            onValueChange = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .width(300.dp)
                .padding(16.dp)
        )
    }
}