package com.jooheon.clean_architecture.features.musicplayer.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicservice.data.albumArtUri
import com.jooheon.clean_architecture.features.common.R
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage

@Composable
fun MediaItemLarge(
    onItemClick: (song: Song) -> Unit,
    song: Song,
    modifier: Modifier = Modifier
) {
    val albumArtImage = song.albumArtUri.toString()
    val artistImage = albumArtImage // FIXME

    Column(
        modifier = modifier
            .clickable { onItemClick(song) }
    ) {
        CoilImage(
            url = albumArtImage,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            placeholderRes = R.drawable.ic_placeholder,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        )

        Row(
            verticalAlignment = Alignment.Top,
            modifier = modifier
        ) {
            CoilImage(
                url = artistImage,
                contentDescription = song.artist,
                placeholderRes = R.drawable.test_2,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
            ) {
                Text(
                    text = song.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = song.artist,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Preview
@Composable
fun MediaItemLargePreview() {
    PreviewTheme(false) {
        MediaItemLarge(
            onItemClick = {},
            song = Song.default.copy(
                title = "song title - 123",
                artist = "song artist name - 456"
            ),
            modifier = Modifier.width(400.dp)
        )
    }
}