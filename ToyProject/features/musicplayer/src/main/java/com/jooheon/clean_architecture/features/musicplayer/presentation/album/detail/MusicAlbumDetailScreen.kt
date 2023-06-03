package com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail

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
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenState
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
fun MusicAlbumDetailScreen(
    musicAlbumDetailScreenState: MusicAlbumDetailScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,

    onMusicAlbumDetailScreenEvent: (MusicAlbumDetailScreenEvent) -> Unit,
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
                    text = musicAlbumDetailScreenState.album.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { onMusicAlbumDetailScreenEvent(MusicAlbumDetailScreenEvent.OnBackClick) }
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
                AlbumDetailMediaColumn(
                    album = musicAlbumDetailScreenState.album,
                    listState = listState,
                    onEvent = onMusicAlbumDetailScreenEvent
                )
            }
        )
    }

    BackHandler {
        onMusicAlbumDetailScreenEvent(MusicAlbumDetailScreenEvent.OnBackClick)
    }
}

@Composable
private fun AlbumDetailMediaColumn(
    album: Album,
    listState: LazyListState = rememberLazyListState(),
    onEvent: (MusicAlbumDetailScreenEvent) -> Unit,
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
            item {
                MediaItemSmall(
                    imageUrl = album.songs.firstOrNull()?.imageUrl.defaultEmpty(),
                    title = album.name,
                    subTitle = UiText.StringResource(R.string.song, album.songs.size).asString(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onItemClick = { /** Nothing **/ },
                )

                MediaDetailHeader(
                    count = album.songs.size
                )
            }

            items(
                items = album.songs,
                key = { song: Song -> song.hashCode() }
            ) {song ->
                MediaItemSmallWithoutImage(
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    onItemClick = { onEvent(MusicAlbumDetailScreenEvent.OnSongClick(song)) }
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


@Preview
@Composable
private fun MusicAlbumDetailScreenPreview() {
    PreviewTheme(false) {
        MusicAlbumDetailScreen(
            musicAlbumDetailScreenState = MusicAlbumDetailScreenState.default,
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onMusicAlbumDetailScreenEvent = {},
            onMusicPlayerScreenEvent = {},
        )
    }
}