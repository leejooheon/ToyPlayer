package com.jooheon.clean_architecture.features.musicplayer.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaItemSmall

@Composable
fun MusicAlbumScreen(
    state: MusicAlbumScreenState,
    onEvent: (MusicAlbumScreenEvent) -> Unit,
) {
    val listState = rememberLazyListState()

    AlbumMediaColumn(
        albumList = state.albums,
        listState = listState,
        onItemClick = { onEvent(MusicAlbumScreenEvent.OnAlbumItemClick(it)) }
    )
}

@Composable
private fun AlbumMediaColumn(
    albumList: List<Album>,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (Album) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(
            items = albumList,
            key = { album: Album -> album.id }
        ) { album ->
            MediaItemSmall(
                imageUrl = album.songs.firstOrNull()?.imageUrl.defaultEmpty(),
                title = album.name,
                subTitle = album.artist,
                modifier = Modifier,
                onItemClick = { onItemClick(album) },
            )
        }

        // BottomMusicPlayer padding
        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Preview
@Composable
private fun MusicAlbumScreenPreview() {
    PreviewTheme(false) {
        MusicAlbumScreen(
            state = MusicAlbumScreenState.default,
            onEvent = {}
        )
    }
}