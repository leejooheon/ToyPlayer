package com.jooheon.toyplayer.features.player.component.info.content.component

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.player.common.contentWidth
import com.jooheon.toyplayer.features.player.common.horizontalMargin
import com.jooheon.toyplayer.features.player.model.PlayerUiState

@Composable
internal fun ContentItem(
    state: LazyListState,
    model: PlayerUiState.ContentModel,
    currentSong: Song,
    titleAlpha: Float,
    isPlaying: Boolean,
    onContentClick: (playlistId: Int, startIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // when dp changed, change cardTopPreviewHeight together
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalMargin())
                .alpha(titleAlpha)
        ) {
            Text(
                text = model.playlist.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = androidx.compose.ui.graphics.Color.White,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )
        }
        // when dp changed, change cardTopPreviewHeight together
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            state = state,
            horizontalArrangement = Arrangement.spacedBy(contentWidth() * 0.1f),
            contentPadding = PaddingValues(
                horizontal = horizontalMargin()
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(model.playlist.songs) { index, item ->
                ContentCardItem(
                    title = item.title,
                    imageUrl = item.imageUrl,
                    isPlaying = isPlaying,
                    isSelectedItem = item.key() == currentSong.key(),
                    onClick = { onContentClick.invoke(model.playlist.id, index) },
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
            state = rememberLazyListState(),
            model = PlayerUiState.ContentModel.preview,
            currentSong = Song.preview,
            titleAlpha = 1f,
            isPlaying = false,
            onContentClick = { _, _ -> },
        )
    }
}