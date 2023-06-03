package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaDetailHeader
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaItemSmall
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaItemSmallWithoutImage
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicArtistDetailScreen(
    musicArtistDetailScreenState: MusicArtistDetailScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,

    onMusicArtistDetailScreenEvent: (MusicArtistDetailScreenEvent) -> Unit,
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SmallTopAppBar(
            title = {
                Text(
                    text = musicArtistDetailScreenState.artist.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { onMusicArtistDetailScreenEvent(MusicArtistDetailScreenEvent.OnBackClick) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "back",
                    )
                }
            }
        )

        MediaSwipeableLayout(
            musicPlayerScreenState = musicPlayerScreenState,
            swipeableState = swipeableState,
            swipeAreaHeight = swipeAreaHeight,
            motionProgress = motionProgress,
            onEvent = onMusicPlayerScreenEvent,
            content = {
                ArtistDetailMediaColumn(
                    artist = musicArtistDetailScreenState.artist,
                    listState = listState,
                    onEvent = onMusicArtistDetailScreenEvent
                )
            }
        )
    }

    BackHandler {
        onMusicArtistDetailScreenEvent(MusicArtistDetailScreenEvent.OnBackClick)
    }
}

@Composable
private fun ArtistDetailMediaColumn(
    artist: Artist,
    listState: LazyListState = rememberLazyListState(),
    onEvent: (MusicArtistDetailScreenEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(
                items = artist.albums,
                key = { album: Album -> album.hashCode() }
            ) { album ->
                MediaItemSmall(
                    imageUrl = album.songs.firstOrNull()?.imageUrl.defaultEmpty(),
                    title = album.name,
                    subTitle = album.artist,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onItemClick = { /** Nothing **/ },
                )

                MediaDetailHeader(count = album.songs.size)

                album.songs.forEach { song ->
                    MediaItemSmallWithoutImage(
                        title = song.title,
                        subTitle = "${song.artist} • ${song.album}",
                        onItemClick = { onEvent(MusicArtistDetailScreenEvent.OnSongClick(song)) }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun MusicArtistDetailScreenPreview() {
    PreviewTheme(false) {
        MusicArtistDetailScreen(
            musicArtistDetailScreenState = MusicArtistDetailScreenState.default,
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onMusicArtistDetailScreenEvent = {},
            onMusicPlayerScreenEvent = {},
        )
    }
}