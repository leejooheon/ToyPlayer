package com.jooheon.clean_architecture.features.musicplayer.presentation.artist

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaItemSmall
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import java.lang.Float
import kotlin.math.max

//class MusicArtistScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicArtistScreen(
    musicArtistState: MusicArtistScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,
    onMusicArtistScreenEvent: (MusicArtistScreenEvent) -> Unit,
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
            ArtistMediaColumn(
                artists = musicArtistState.artists,
                listState = listState,
                onItemClick = { onMusicArtistScreenEvent(MusicArtistScreenEvent.OnArtistItemClick(it)) }
            )
        }
    )
}

@Composable
private fun ArtistMediaColumn(
    artists: List<Artist>,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (Artist) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        items(
            items = artists,
            key = { artist: Artist -> artist.id }
        ) { artist ->
            val size = artist.albums.map { it.songs }.flatten().size
            MediaItemSmall(
                imageUrl = artist.albums.firstOrNull()?.songs?.firstOrNull()?.imageUrl.defaultEmpty(),
                title = artist.name,
                subTitle = UiText.StringResource(R.string.song, size).asString(),
                modifier = Modifier,
                onItemClick = { onItemClick(artist) },
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
private fun MusicArtistScreenPreview() {
    PreviewTheme(false) {
        MusicArtistScreen(
            musicArtistState = MusicArtistScreenState.default,
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onMusicArtistScreenEvent = {},
            onMusicPlayerScreenEvent = {},
        )
    }
}