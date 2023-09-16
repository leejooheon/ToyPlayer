package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.toyproject.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.MediaDetailHeader
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.MediaItemSmallNoImage
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.controller.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue.model.MusicPlayingQueueScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue.model.MusicPlayingQueueScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.detail.components.MusicPlaylistDetailHeader

import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayingQueueScreen(
    musicPlayingQueueScreenState: MusicPlayingQueueScreenState,
    onMusicPlayingQueueScreenEvent: (MusicPlayingQueueScreenEvent) -> Unit,
    onMediaDropDownMenuEvent: (MusicMediaItemEvent) -> Unit,

    musicPlayerState: MusicPlayerState,
    onMusicPlayerEvent: (MusicPlayerEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SmallTopAppBar(
            title = {
                Text(
                    text = musicPlayingQueueScreenState.playlist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { onMusicPlayingQueueScreenEvent(MusicPlayingQueueScreenEvent.OnBackClick) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "back",
                    )
                }
            }
        )
        MediaSwipeableLayout(
            musicPlayerState = musicPlayerState,
            swipeableState = swipeableState,
            swipeAreaHeight = swipeAreaHeight,
            motionProgress = motionProgress,
            onEvent = onMusicPlayerEvent,
            content = {
                PlayingQueueMediaColumn(
                    listState = rememberLazyListState(),
                    playlist = musicPlayingQueueScreenState.playlist,
                    onPlayClick = { onMusicPlayingQueueScreenEvent(MusicPlayingQueueScreenEvent.OnSongClick(it)) },
                    onActionPlayAll = { onMusicPlayingQueueScreenEvent(MusicPlayingQueueScreenEvent.OnActionPlayAll(it)) },
                    onDropDownEvent = { index, song ->
                        when(index) {
                            0 -> onMusicPlayingQueueScreenEvent(MusicPlayingQueueScreenEvent.OnDeleteClick(song))
                            1 -> onMediaDropDownMenuEvent(MusicDropDownMenuState.indexToEvent(index, song))
                            else -> { /** Nothing **/ }
                        }
                    },
                )
            }
        )
    }
}

@Composable
private fun PlayingQueueMediaColumn(
    listState: LazyListState,
    playlist: Playlist,
    onPlayClick: (Song) -> Unit,
    onActionPlayAll: (Boolean) -> Unit,
    onDropDownEvent: (Int, Song) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            item {
                MusicPlaylistDetailHeader(
                    playlist = playlist,
                    onPlayAllClick = { onActionPlayAll(false) },
                    onPlayAllWithShuffleClick = { onActionPlayAll(true) },
                )
            }

            item {
                MediaDetailHeader(
                    count = playlist.songs.size
                )
            }

            itemsIndexed(
                items = playlist.songs,
            ) { index, song ->
                MediaItemSmallNoImage(
                    trackNumber = index + 1,
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    duration = MusicUtil.toReadableDurationString(song.duration),
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.playlistMediaItems),
                    onItemClick = { onPlayClick(song)},
                    onDropDownMenuClick = { onDropDownEvent(it, song) }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    )
}

@Preview
@Composable
private fun MusicPlaylistDetailScreenPreview() {
    PreviewTheme(true) {
        MusicPlayingQueueScreen(
            musicPlayingQueueScreenState = MusicPlayingQueueScreenState.default.copy(
                playlist = Playlist.default.copy(
                    name = UiText.StringResource(R.string.placeholder_long).asString()
                )
            ),
            onMusicPlayingQueueScreenEvent = {},
            onMediaDropDownMenuEvent = {},

            musicPlayerState = MusicPlayerState.default,
            onMusicPlayerEvent = {},
        )
    }
}
