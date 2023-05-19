package com.jooheon.clean_architecture.features.musicplayer.screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme

@Composable
fun MediaColumn(
    onItemClick: (song: Song) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    playlist: List<Song>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(playlist) { song ->
            MediaItemLarge(
                onItemClick = onItemClick,
                song = song,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
fun VideoColumnPreview() {
    PreviewTheme(false) {
        Surface(modifier = Modifier.width(400.dp)) {
            MediaColumn(
                onItemClick = {},
                playlist = listOf(Song.default),
            )
        }
    }
}