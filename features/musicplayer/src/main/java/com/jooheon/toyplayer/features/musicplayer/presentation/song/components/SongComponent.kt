package com.jooheon.toyplayer.features.musicplayer.presentation.song.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.components.CoilImage
import com.jooheon.toyplayer.features.common.compose.extensions.toDp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.common.utils.MusicUtil.defaultImgaeUri
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicservice.ext.albumArtUri
import com.jooheon.toyplayer.core.strings.UiText

@Composable
fun SongComponent(
    modifier: Modifier = Modifier,
    dataSet: List<Song>,
    onMusicSongScreenEvent: (MusicSongScreenEvent) -> Unit,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        SongComponentContent(
            dataSet = dataSet,
            constraint = constraints,
            onClick = {
                if(it.key() == Song.default.key()) {
                    onMusicSongScreenEvent(
                        MusicSongScreenEvent.OnMusicComponentClick(MusicListType.All)
                    )
                } else onMusicPlayerEvent(MusicPlayerEvent.OnSongClick(it))
            }
        )
    }
}

@Composable
private fun SongComponentContent(
    dataSet: List<Song>,
    constraint: Constraints,
    onClick: (Song) -> Unit,
) {
    when(dataSet.size) {
        0 -> return // show no local song available
        1, 2, 3 -> Unit // maybe something else
    }

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
                items.forEachIndexed { songIndex, song ->
                    if(index + 1 == chunkedItems.size && songIndex + 1 == items.size) { // last item
                        SongComponentHorizontalItem(
                            song = Song.default.copy(
                                imageUrl = defaultImgaeUri().toString(),
                                title = UiText.StringResource(R.string.action_see_more).asString() + " ${dataSet.size}"
                            ),
                            onClick = onClick
                        )
                    } else SongComponentHorizontalItem(
                        song = song,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun SongComponentHorizontalItem(
    song: Song,
    onClick: (Song) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f, true)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick(song) }
    ) {
        CoilImage(
            url = song.albumArtUri.toString(),
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            val typography = MaterialTheme.typography.labelMedium
            Text(
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .wrapContentHeight(Alignment.Bottom),
                text = song.title,
                color = MaterialTheme.colorScheme.surface,
                style = typography.copy(shadow = Shadow(Color.Black, Offset(1f,1f), 1f)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
@Composable
@Preview
private fun PreviewSongComponent() {
    ToyPlayerTheme {
        val dataSet = Song.defaultList + Song.defaultList
        Column {
            MusicSongCommonHeader(
                title = UiText.DynamicString("Title"),
                resId = R.drawable.default_album_art
            )

            SongComponent(
                modifier = Modifier,
                dataSet = dataSet,
                onMusicSongScreenEvent = {},
                onMusicPlayerEvent = {},
            )
        }
    }
}