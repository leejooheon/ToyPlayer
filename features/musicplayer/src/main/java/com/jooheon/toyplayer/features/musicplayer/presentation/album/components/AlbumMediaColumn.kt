package com.jooheon.toyplayer.features.musicplayer.presentation.album.components

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
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.album.model.MusicAlbumScreenState


@Composable
internal fun AlbumMediaColumn(
    albumList: List<Album>,
    listState: LazyGridState = rememberLazyGridState(),
    onItemClick: (Album) -> Unit,
) {
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 16.dp
        ),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            items(
                items = albumList,
                key = { album: Album -> album.hashCode() }
            ) { album ->
                AlbumMediaColumnItem(
                    album = album,
                    onItemClick = onItemClick
                )
            }
        }
    )
}

@Preview
@Composable
private fun AlbumMediaColumnPreview() {
    ToyPlayerTheme {
        AlbumMediaColumn(
            albumList = MusicAlbumScreenState.default.albums,
            listState = rememberLazyGridState(),
            onItemClick = {},
        )
    }
}