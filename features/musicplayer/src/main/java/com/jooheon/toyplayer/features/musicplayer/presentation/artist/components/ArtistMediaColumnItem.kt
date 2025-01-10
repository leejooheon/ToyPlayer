package com.jooheon.toyplayer.features.musicplayer.presentation.artist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.features.common.compose.components.CoilImage
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.core.strings.UiText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArtistMediaColumnItem(
    artist: Artist,
    onItemClick: () -> Unit
) {
    val imageUrl = artist.albums.firstOrNull()?.songs?.firstOrNull()?.imageUrl.defaultEmpty()

    val songSize = artist.albums.map { it.songs }.flatten().size
    val albumSize = artist.albums.size

    val songCount = UiText.StringResource(R.string.n_song, songSize).asString()
    val albumCount = UiText.StringResource(R.string.n_album, albumSize).asString()

    Card(
        onClick = onItemClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(4.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CoilImage(
                url = imageUrl,
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .aspectRatio(1f)
                    .clip(RectangleShape)
            )

            Text(
                text = artist.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = "$songCount • $albumCount",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
@Preview
@Composable
private fun ArtistMediaColumnItemPreview() {
    ToyPlayerTheme {
        ArtistMediaColumnItem(
            artist = Artist.default.copy(
                name = Resource.longStringPlaceholder,
                albums = Album.defaultList,
            ),
            onItemClick = {}
        )
    }
}