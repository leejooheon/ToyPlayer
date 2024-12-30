package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicservice.ext.albumArtUri
import com.jooheon.toyplayer.features.common.compose.components.AsyncImageTest
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme

@Composable
internal fun CachedSongItem(
    song: Song,
    onItemClick: (Song) -> Unit,
) {
    val itemWidth = 96.dp
    val thumbnailSize = 96.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .width(itemWidth)
            .clickable(onClick = { onItemClick(song) })
    ) {
        AsyncImageTest(
            url = song.albumArtUri.toString(),
            size = thumbnailSize,
            contentDescription = song.title,
            shape = RoundedCornerShape(4.dp)
        )

        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

}

@Preview
@Composable
private fun PreviewCachedSongItem() {
    val context = LocalContext.current
    ToyPlayerTheme {
        CachedSongItem(
            song = Song.default,
            onItemClick = {},
        )
    }
}