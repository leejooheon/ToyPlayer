package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.MusicDropDownMenuState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.MusicDropDownMenu
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.PlaylistDropDownMenuEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model.MusicPlaylistScreenState
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicPlaylistScreen(
    musicPlaylistScreenState: MusicPlaylistScreenState,
    musicPlayerScreenState: MusicPlayerScreenState,

    onPlaylistDropDownMenuEvent: (PlaylistDropDownMenuEvent) -> Unit,
    onMusicPlaylistScreenEvent: (MusicPlaylistScreenEvent) -> Unit,

    onMusicPlayerScreenEvent: (MusicPlayerScreenEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)

    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onMusicPlayerScreenEvent,
        content = {
            PlaylistMediaColumn(
                listState = rememberLazyListState(),
                playlists = musicPlaylistScreenState.playlists ,
                onItemClick = { onMusicPlaylistScreenEvent(MusicPlaylistScreenEvent.onPlaylistClick(it)) },
                onAddPlaylistClick = { onMusicPlaylistScreenEvent(MusicPlaylistScreenEvent.onAddPlaylist(it)) },
                onDropDownMenuClick = { index, playlist ->
                    val event = PlaylistDropDownMenuEvent.fromIndex(index, playlist)
                    onPlaylistDropDownMenuEvent(event)
                }
            )
        }
    )
}

@Composable
private fun PlaylistMediaColumn(
    listState: LazyListState,
    playlists: List<Playlist>,
    onItemClick: (Playlist) -> Unit,
    onAddPlaylistClick: (String) -> Unit,
    onDropDownMenuClick: (Int, Playlist) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 16.dp
        ),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            item {
                PlaylistHeader(
                    onAddPlaylistClick = onAddPlaylistClick
                )
            }

            items(
                items = playlists,
                key = { playlist: Playlist -> playlist.hashCode() }
            ) { playlist ->
                PlaylistColumnItem(
                    playlist = playlist,
                    onItemClick = { onItemClick(playlist) },
                    onDropDownMenuClick = onDropDownMenuClick,
                )
            }
        }
    )
}

@Composable
private fun PlaylistHeader(
    onAddPlaylistClick: (String) -> Unit,
) {
    var playlistDialogState by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {  },
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = UiText.StringResource(R.string.option_see_more).asString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                    )
                }
            }
        )

        IconButton(
            onClick = { playlistDialogState = true},
            content = {
                Icon(
                    imageVector = Icons.Filled.PlaylistAdd,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = UiText.StringResource(R.string.option_add_playlist).asString()
                )
            }
        )
    }

    MusicPlaylistDialog(
        openDialog = playlistDialogState,
        title = UiText.StringResource(R.string.dialog_new_playlist).asString(),
        name = "",
        onConfirmButtonClicked = onAddPlaylistClick,
        onDismiss = { playlistDialogState = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistColumnItem(
    playlist: Playlist,
    onItemClick: () -> Unit,
    onDropDownMenuClick: (index: Int, playlist: Playlist) -> Unit,
) {
    val playlistNameEditIndex = 1
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    var playlistDialogState by remember { mutableStateOf(false) }

    Card(
        onClick = onItemClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.PlaylistPlay,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null,
                modifier = Modifier.weight(0.1f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            CoilImage(
                url = playlist.thumbnailUrl,
                contentDescription = playlist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(25))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(0.7f)
            ) {
                val subTitle = UiText.StringResource(R.string.n_song, playlist.songs.size).asString() + " • " +
                        MusicUtil.toReadableDurationString(playlist.songs.fastSumBy { it.duration.toInt() }.toLong())
                Text(
                    text = playlist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    ),
                )
            }

            IconButton(
                onClick = { dropDownMenuExpanded = true },
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "more" // TODO
                )
                MusicDropDownMenu(
                    expanded = dropDownMenuExpanded,
                    dropDownMenuState = MusicDropDownMenuState(MusicDropDownMenuState.playlistItems),
                    onDismissRequest = { dropDownMenuExpanded = false },
                    onClick = {
                        if(it == playlistNameEditIndex) {
                            playlistDialogState = true
                        } else {
                            onDropDownMenuClick(it, playlist)
                        }
                    }
                )
            }
        }
    }

    MusicPlaylistDialog(
        openDialog = playlistDialogState,
        title = UiText.StringResource(R.string.dialog_edit_playlist_name).asString(),
        name = playlist.name,
        onConfirmButtonClicked = {
            onDropDownMenuClick(playlistNameEditIndex, playlist.copy(name = it))
        },
        onDismiss = { playlistDialogState = false }
    )
}

@Preview
@Composable
private fun MusicPlaylistScreenPreview() {
    PreviewTheme(false) {
        MusicPlaylistScreen(
            musicPlaylistScreenState = MusicPlaylistScreenState.default,
            musicPlayerScreenState = MusicPlayerScreenState.default,

            onMusicPlaylistScreenEvent = {},
            onMusicPlayerScreenEvent = {},

            onPlaylistDropDownMenuEvent = {},
        )
    }
}