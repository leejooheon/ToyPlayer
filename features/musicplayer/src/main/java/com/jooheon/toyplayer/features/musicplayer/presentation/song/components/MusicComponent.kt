package com.jooheon.toyplayer.features.musicplayer.presentation.song.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Stream
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.features.common.compose.extensions.toDp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.song.model.MusicComponentModel
import com.jooheon.toyplayer.features.musicplayer.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.song.model.MusicSongScreenState
import com.jooheon.toyplayer.core.strings.UiText
import com.jooheon.toyplayer.domain.model.music.MusicListType

@Composable
fun MusicComponent(
    modifier: Modifier = Modifier,
    state: MusicSongScreenState,
    onMusicSongScreenEvent: (MusicSongScreenEvent) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        MusicComponentContent(
            dataSet = listOf(
                MusicComponentModel(
                    title = UiText.DynamicString("Local Music"),
                    iconImageVector =  Icons.Outlined.LocalActivity,
                    type = MusicListType.Local,
                ),
                MusicComponentModel(
                    title = UiText.DynamicString("Stream Music"),
                    iconImageVector =  Icons.Outlined.Stream,
                    type = MusicListType.Streaming,
                ),
                MusicComponentModel(
                    title = UiText.DynamicString("Asset Music"),
                    iconImageVector =  Icons.Outlined.WebAsset,
                    type = MusicListType.Asset,
                ),
            ),
            constraint = constraints,
            onClick = {
                onMusicSongScreenEvent(MusicSongScreenEvent.OnMusicComponentClick(it))
            }
        )
    }
}

@Composable
private fun MusicComponentContent(
    dataSet: List<MusicComponentModel>,
    constraint: Constraints,
    onClick: (MusicListType) -> Unit
) {

    val childItemSize = 3
    val childItemContentSize = (constraint.maxWidth - 48 * 2 - 16 * 2) / childItemSize
    val chunkedItems = dataSet.chunked(childItemSize).take(2)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(chunkedItems.size) { index ->
            val items = chunkedItems[index]
            if (items.isEmpty()) return
            Row(
                modifier = Modifier
                    .width(constraint.maxWidth.toDp())
                    .height(childItemContentSize.toDp()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach {
                    MusicComponentHorizontalItem(
                        model = it,
                        onClick = onClick
                    )
                }
            }
        }
    }
}
@Composable
private fun MusicComponentHorizontalItem(
    model: MusicComponentModel,
    onClick: (MusicListType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f, true)
            .clip(RoundedCornerShape(4.dp))
            .composed {
                val color =
                    androidx.compose.material3.MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                val alpha = 0.8f
                drawBehind { drawRect(color, alpha = alpha) }
            }
            .clickable { onClick(model.type) }
            .padding(horizontal = 8.dp),
        propagateMinConstraints = true
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier.padding(8.dp),
                imageVector = model.iconImageVector,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                contentDescription = null,
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = model.title.asString(),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

        }
    }
}

@Composable
@Preview
private fun PreviewSongComponent() {
    ToyPlayerTheme {
        Column {
            MusicSongCommonHeader(
                title = UiText.DynamicString("Title"),
                resId = R.drawable.default_album_art
            )

            MusicComponent(
                modifier = Modifier,
                state = MusicSongScreenState.default,
                onMusicSongScreenEvent = {}
            )
        }
    }
}