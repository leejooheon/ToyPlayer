package com.jooheon.toyplayer.features.player.component.info.content

import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.common.extension.showFavorite
import com.jooheon.toyplayer.features.player.common.contentWidth
import com.jooheon.toyplayer.features.player.common.horizontalMargin

@Composable
internal fun ContentItem(
    playlist: Playlist,
    currentSong: Song,
    titleAlpha: Float,
    isPlaying: Boolean,
    onContentClick: (index: Int) -> Unit,
    onFavoriteClick: (song: Song) -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalMargin(),
                    top = 8.dp,
                    bottom = 8.dp,
                )
                .alpha(titleAlpha),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = androidx.compose.ui.graphics.Color.White,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onDetailsClick.invoke() }
            )

            IconButton(
                onClick = onDetailsClick,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = UiText.StringResource(Strings.back).asString(),
                    tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.offset(y = 1.dp)
                )
            }
        }

        LazyRow(
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(contentWidth() * 0.1f),
            contentPadding = PaddingValues(
                horizontal = horizontalMargin()
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(playlist.songs) { index, item ->
                ContentCardItem(
                    title = item.title,
                    imageUrl = item.imageUrl,
                    isPlaying = isPlaying,
                    isSelectedItem = item.key() == currentSong.key(),
                    isFavorite = item.isFavorite,
                    showFavorite = playlist.showFavorite(),
                    onClick = { onContentClick.invoke(index) },
                    onFavoriteClick = { onFavoriteClick.invoke(item)},
                    modifier = Modifier.width(contentWidth())
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = Color.GRAY.toLong()
)
@Composable
private fun PreviewContentItem() {
    ToyPlayerTheme {
        ContentItem(
            playlist = Playlist.preview,
            currentSong = Song.preview,
            titleAlpha = 1f,
            isPlaying = true,
            onContentClick = {},
            onFavoriteClick = {},
            onDetailsClick = {},
        )
    }
}