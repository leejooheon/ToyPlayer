package com.jooheon.clean_architecture.features.musicplayer.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaItemSmall
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicAlbumScreen(
    musicAlbumState: MusicAlbumScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,
    onMusicAlbumEvent: (MusicAlbumScreenEvent) -> Unit,
    onMusicPlayerScreenEvent: (MusicPlayerScreenEvent) -> Unit,
) {

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val listState = rememberLazyListState()

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerScreenEvent,
        content = {
            AlbumMediaColumn(
                albumList = musicAlbumState.albums,
                listState = listState,
                onItemClick = { onMusicAlbumEvent(MusicAlbumScreenEvent.OnAlbumItemClick(it)) }
            )
        }
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
            musicAlbumState = MusicAlbumScreenState.default,
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onMusicAlbumEvent = {},
            onMusicPlayerScreenEvent = {},
        )
    }
}