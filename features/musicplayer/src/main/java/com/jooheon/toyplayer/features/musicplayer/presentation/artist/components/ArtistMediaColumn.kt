package com.jooheon.toyplayer.features.musicplayer.presentation.artist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme

@Composable
internal fun ArtistMediaColumn(
    artists: List<com.jooheon.toyplayer.domain.model.music.Artist>,
    listState: LazyGridState,
    onItemClick: (com.jooheon.toyplayer.domain.model.music.Artist) -> Unit,
) {
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 16.dp
        ),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            items(
                items = artists,
                key = { artist: com.jooheon.toyplayer.domain.model.music.Artist -> artist.hashCode() }
            ) { artist ->
                ArtistMediaColumnItem(
                    artist = artist,
                    onItemClick = { onItemClick(artist) }
                )
            }
        }
    )
}
@Preview
@Composable
private fun ArtistMediaColumnPreview() {
    ToyPlayerTheme {
        ArtistMediaColumn(
            artists = listOf(Artist.default),
            listState = rememberLazyGridState(),
            onItemClick = {},
        )
    }
}