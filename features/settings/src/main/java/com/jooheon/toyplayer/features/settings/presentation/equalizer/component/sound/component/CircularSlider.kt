package com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.features.settings.presentation.equalizer.component.sound.toDegrees
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.*

@Composable
internal fun CircularSlider(
    title: UiText,
    initialValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var angle by remember { mutableFloatStateOf(((initialValue - 1) / 99f) * 270f) }

    val unFilledColor = MaterialTheme.colorScheme.background
    val filledColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(Unit) {
        snapshotFlow { angle }
            .debounce(200)
            .map { ((it / 270f) * 99f).roundToInt() + 1 }
            .distinctUntilChanged()
            .collectLatest(onValueChange)
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
                        val fixedTheta = (theta + 360f + 135f) % 360f // Rotate 135 deg offset
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

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${((angle / 270f) * 100).toInt()}%",
                    fontSize = 20.sp,
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
private fun PreviewCircularSlider() {
    ToyPlayerTheme {
        CircularSlider(
            title = UiText.DynamicString("label"),
            initialValue = 50,
            onValueChange = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .width(300.dp)
                .padding(16.dp)
        )
    }
}