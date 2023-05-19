package com.jooheon.clean_architecture.presentation.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme


@Composable
private fun SubwayWidgetTest(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {

        StationItem(SubWayInfo.TEST_ONE)
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
        StationItem(SubWayInfo.TEST_TWO)
    }
}
@Composable
private fun StationItem(subWayInfo: SubWayInfo) {
    Row {
        Text(
            text = "${subWayInfo.stationName}역",
            style = TextStyle(
                color = parseColor(MaterialTheme.colorScheme.primary),
            )
        )
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        Column {
            StationDetailItem(
                train = subWayInfo.upLineTrain,
                firstTime = subWayInfo.upLineTime.first(),
                secondTime = subWayInfo.upLineTime.last()
            )

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            StationDetailItem(
                train = subWayInfo.downLineTrain,
                firstTime = subWayInfo.downLineTime.first(),
                secondTime = subWayInfo.downLineTime.last()
            )
        }
    }
}

@Composable
private fun StationDetailItem(
    train: String,
    firstTime: String,
    secondTime: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.25f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = train,
            style = TextStyle(
                color = parseColor(MaterialTheme.colorScheme.error),
                fontSize = 4.sp,
            ),
        )
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        Text(
            modifier = Modifier.weight(0.25f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = firstTime,
            style = TextStyle(
                color = parseColor(MaterialTheme.colorScheme.error),
                fontSize = 8.sp
            )
        )
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        Text(
            modifier = Modifier.weight(0.5f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            text = secondTime,
            style = TextStyle(
                color = parseColor(MaterialTheme.colorScheme.error),
                fontSize = 8.sp
            )
        )
    }
}


@Composable
private fun parseColor(color: Color) = color

@Composable
@Preview
private fun PreviewSubwayWidget() {

    PreviewTheme {
        SubwayWidgetTest(
            modifier = Modifier.width(140.dp).height(60.dp)
        )
    }
}