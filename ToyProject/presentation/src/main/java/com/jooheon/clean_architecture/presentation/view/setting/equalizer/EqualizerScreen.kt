package com.jooheon.clean_architecture.presentation.view.setting.equalizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.view.main.sharedViewModel
import com.jooheon.clean_architecture.presentation.view.setting.SettingViewModel
import com.jooheon.clean_architecture.presentation.view.setting.language.LanguageScreen
import com.jooheon.clean_architecture.presentation.view.temp.EmptySettingUseCase

@Composable
internal fun EqualizerScreen(
    navigator: NavController,
    viewModel: SettingViewModel = hiltViewModel(sharedViewModel())
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        CubicChart(
            yPoints = listOf(
                199f, 52f, 193f, 290f, 150f, 445f
            )
        )
    }
}
@OptIn(ExperimentalTextApi::class)
@Composable
fun CubicChart(
    modifier: Modifier = Modifier,
    yPoints: List<Float>,
    graphColor: Color = Color.Green
) {

    val normX = mutableListOf<Float>()
    val normY = mutableListOf<Float>()
    val textMeasure = rememberTextMeasurer()

    val spacing = 100f
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .height(200.dp),
            onDraw = {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset.Zero,
                    size = Size(
                        width = size.width,
                        height = size.height
                    ),
                    style = Stroke()
                )

                val spacePerHour = (size.width - spacing) / yPoints.size


                val strokePath = Path().apply {

                    for (i in yPoints.indices) {
                        val currentX = spacing + i * spacePerHour

                        if (i == 0) {
                            moveTo(currentX, yPoints[i])
                        } else {
                            val previousX = spacing + (i - 1) * spacePerHour

                            val conX1 = (previousX + currentX) / 2f
                            val conX2 = (previousX + currentX) / 2f

                            val conY1 = yPoints[i - 1]
                            val conY2 = yPoints[i]

                            cubicTo(
                                x1 = conX1,
                                y1 = conY1,
                                x2 = conX2,
                                y2 = conY2,
                                x3 = currentX,
                                y3 = yPoints[i]
                            )
                        }
                        // Circle dot points
                        normX.add(currentX)
                        normY.add(yPoints[i])
                    }
                }

                drawPath(
                    path = strokePath,
                    color = graphColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                (normX.indices).forEachIndexed { index, value ->
                    drawCircle(
                        color = Color.Black,
                        radius = 3.dp.toPx(),
                        center = Offset(normX[value], normY[value])
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 3.dp.toPx(),
                        center = Offset(normX[value], 200.dp.toPx())
                    )
                    drawText(
                        textMeasurer = textMeasure,
                        text = "${index}th",
                        topLeft = Offset(normX[value], 200.dp.toPx())
                    )
                }
            }
        )
    }
}

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
@Preview
@Composable
private fun PreviewEqualizerScreen() {
    val context = LocalContext.current
    PreviewTheme(false) {
        EqualizerScreen(
            navigator = NavController(context),
            viewModel = SettingViewModel(EmptySettingUseCase())
        )
    }
}