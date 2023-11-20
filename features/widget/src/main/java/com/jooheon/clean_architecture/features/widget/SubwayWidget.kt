package com.jooheon.clean_architecture.features.widget

import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.glance.text.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.jooheon.clean_architecture.features.main.ScreenNavigation

internal class SubwayWidget: GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<List<SubWayInfo>> =
        SubwayInfoDefinition

    @Composable
    override fun Content() {
        val subwayInfoList = currentState<List<SubWayInfo>>()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .clickable(actionRunCallback<SubwayInfoAction>())
//                .clickable(actionStartActivity(getSubwayIntent())),
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clickable(actionRunCallback<SubwayInfoAction>()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_logo_github),
                    contentDescription = "",
                    modifier = GlanceModifier
                        .size(32.dp)
                        .clickable(actionRunCallback<SubwayInfoAction>()),
                )
                Image(
                    provider = ImageProvider(R.drawable.common_google_signin_btn_icon_dark),
                    contentDescription = "",
                    modifier = GlanceModifier
                        .size(32.dp)
                        .clickable(actionStartActivity(getSubwayIntent()))
                )
            }
            if(subwayInfoList.size != 2) {
                EmptyListContent()
            } else {
                SubwayListContent(subwayInfoList)
            }
        }
    }

    @Composable
    private fun EmptyListContent() {
        Box(modifier = GlanceModifier.fillMaxSize().padding(top = 16.dp)) {
            Text(text = "Some error occured.")
        }
    }

    @Composable
    private fun SubwayListContent(subwayInfoList: List<SubWayInfo>) {
        LazyColumn {
            items(items = subwayInfoList) { subwayInfo ->
                StationItem(subWayInfo = subwayInfo)

            }
        }
    }

    @Composable
    private fun StationItem(subWayInfo: SubWayInfo) {
        Row {
            Text(
                modifier = GlanceModifier.padding(horizontal = 2.dp),
                text = "${subWayInfo.stationName}역",
                style = TextStyle(
                    color = parseColor(MaterialTheme.colorScheme.primary),
                )
            )

            Column {
                StationDetailItem(
                    modifier = GlanceModifier
                        .padding(horizontal = 2.dp)
                        .fillMaxWidth(),
                    color = parseColor(MaterialTheme.colorScheme.error),
                    train = subWayInfo.upLineTrain,
                    firstTime = subWayInfo.upLineTime.first(),
                    secondTime = subWayInfo.upLineTime.last()
                )
                StationDetailItem(
                    modifier = GlanceModifier
                        .padding(horizontal = 2.dp)
                        .fillMaxWidth(),
                    color = parseColor(MaterialTheme.colorScheme.tertiary),
                    train = subWayInfo.downLineTrain,
                    firstTime = subWayInfo.downLineTime.first(),
                    secondTime = subWayInfo.downLineTime.last()
                )
            }
        }
    }

    @Composable
    private fun StationDetailItem(
        modifier:GlanceModifier = GlanceModifier,
        color: ColorProvider,
        train: String,
        firstTime: String,
        secondTime: String
    ) {
        Row(modifier = modifier) {
            Text(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(end = 2.dp),
                maxLines = 1,
                text = train,
                style = TextStyle(
                    color = color,
                    fontSize = 8.sp,
                ),
            )

            Text(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(end = 2.dp),
                maxLines = 1,
                text = firstTime,
                style = TextStyle(
                    color = color,
                    fontSize = 12.sp
                )
            )

//            Text(
//                modifier = GlanceModifier
//                    .defaultWeight()
//                    .padding(end = 2.dp),
//                maxLines = 1,
//                text = secondTime,
//                style = TextStyle(
//                    color = color,
//                    fontSize = 12.sp
//                )
//            )
        }
    }

    private fun getSubwayIntent(): Intent = Intent(Intent.ACTION_VIEW, ScreenNavigation.Subway.WidgetDeeplink.toUri())
    @Composable
    private fun parseColor(color: Color) = ColorProvider(color, color)

    @Preview
    @Composable
    private fun PreviewWidget() {
        Content()
    }
}