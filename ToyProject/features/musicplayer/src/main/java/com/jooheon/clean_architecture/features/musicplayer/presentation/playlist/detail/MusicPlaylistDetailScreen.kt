package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.features.common.compose.components.CoilImage
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaDetailHeader
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaItemSmallWithoutImage
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.MediaDropDownMenuEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.detail.model.MusicPlaylistDetailScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.detail.model.MusicPlaylistDetailScreenState

import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicPlaylistDetailScreen(
    musicPlaylistDetailScreenState: MusicPlaylistDetailScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,

    onMusicPlaylistScreenEvent: (MusicPlaylistDetailScreenEvent) -> Unit,

    onMusicPlayerScreenEvent: (MusicPlayerScreenEvent) -> Unit,
    onMediaDropDownMenuEvent: (MediaDropDownMenuEvent) -> Unit,
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
                    text = musicPlaylistDetailScreenState.playlist.name,
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
                    onClick = { onMusicPlaylistScreenEvent(MusicPlaylistDetailScreenEvent.OnBackClick) }
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
                PlaylistDetailMediaColumn(
                    listState = rememberLazyListState(),
                    playlist = musicPlaylistDetailScreenState.playlist,
                    onEvent = onMusicPlaylistScreenEvent,
                    onDropDownEvent = onMediaDropDownMenuEvent,
                )
            }
        )
    }
}

@Composable
private fun PlaylistDetailMediaColumn(
    listState: LazyListState,
    playlist: Playlist,
    onEvent: (MusicPlaylistDetailScreenEvent) -> Unit,
    onDropDownEvent: (MediaDropDownMenuEvent) -> Unit,
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
                    onPlayAllClick = {},
                    onPlayAllWithShuffleClick = {},
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
                MediaItemSmallWithoutImage(
                    trackNumber = index,
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    duration = MusicUtil.toReadableDurationString(song.duration),
                    onItemClick = { onEvent(MusicPlaylistDetailScreenEvent.OnSongClick(song)) },
                    onDropDownMenuClick = {
                        val event = MediaDropDownMenuEvent.fromIndex(it, song)
                        onDropDownEvent(event)
                    }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    )
}

@Composable
private fun MusicPlaylistDetailHeader(
    playlist: Playlist,
    onPlayAllClick: () -> Unit,
    onPlayAllWithShuffleClick: () -> Unit,
) {
    val allDuration = MusicUtil.toReadableDurationString(playlist.songs.fastSumBy { it.duration.toInt() }.toLong())
    val songCount = UiText.StringResource(R.string.n_song, playlist.songs.size).asString()

    Row(modifier = Modifier.fillMaxSize()) {
        CoilImage(
            url = playlist.thumbnailUrl,
            contentDescription = playlist.name,
            placeholderRes = R.drawable.default_album_art,
            modifier = Modifier
                .padding(12.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15))
                .weight(0.3f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = playlist.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge .copy(
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$songCount • $allDuration",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                TextButton(
                    onClick = onPlayAllClick,
                    modifier = Modifier
                        .clip(RoundedCornerShape(25))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .weight(0.75f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
                TextButton(
                    onClick = onPlayAllWithShuffleClick,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .weight(0.25f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MusicPlaylistDetailScreenPreview() {
    PreviewTheme(true) {
        MusicPlaylistDetailScreen(
            musicPlaylistDetailScreenState = MusicPlaylistDetailScreenState.default.copy(
                playlist = Playlist.default.copy(
                    name = UiText.StringResource(R.string.lorem).asString()
                )
            ),
            musicPlayerScreenState = MusicPlayerScreenState.default,

            onMusicPlaylistScreenEvent = {},
            onMusicPlayerScreenEvent = {},
            onMediaDropDownMenuEvent = {},
        )
    }
}
