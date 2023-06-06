package com.jooheon.clean_architecture.features.musicplayer.presentation.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.compose.extensions.scrollEnabled
import com.jooheon.clean_architecture.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.player.model.MusicPlayerScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.mediaitem.MediaItemLarge
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.mediaitem.MediaItemSmall
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MediaSwipeableLayout
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.MusicOptionDialog
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.mediaitem.MusicDropDownMenuState
import com.jooheon.clean_architecture.features.musicplayer.presentation.event.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import kotlinx.coroutines.launch
import java.lang.Float
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicSongScreen(
    musicPlayerScreenState: MusicPlayerScreenState,

    onEvent: (MusicPlayerScreenEvent) -> Unit,
    onMusicMediaItemEvent: (MusicMediaItemEvent) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val musicState = musicPlayerScreenState.musicState

    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    val swipeAreaHeight = screenHeight - 400

    val swipeableState = rememberSwipeableState(0)
    val swipeProgress = swipeableState.offset.value / -swipeAreaHeight
    val motionProgress = max(Float.min(swipeProgress, 1f), 0f)

    var openDialog by remember { mutableStateOf(false) }
    var viewType by rememberSaveable { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    MediaSwipeableLayout(
        musicPlayerScreenState = musicPlayerScreenState,
        swipeableState = swipeableState,
        swipeAreaHeight = swipeAreaHeight,
        motionProgress = motionProgress,
        onEvent = onEvent,
        content = {
            MusicSongHeader(
                viewType = viewType,
                onSeeMoreButtonClick = { openDialog = true },
                onViewTypeClick = { viewType = it} ,
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
            )

            SongMediaColumn(
                musicState = musicState,
                songMediaColumnItemType = viewType,
                listState = listState,
                onSongClick = {
                    if (swipeableState.currentValue == 0) {
                        if (musicState.currentPlayingMusic != it) {
                            onEvent(MusicPlayerScreenEvent.OnPlayClick(it))
                        } else {
                            scope.launch {
                                swipeableState.animateTo(1)
                            }
                        }
                    }
                },
                onMediaItemEvent = onMusicMediaItemEvent,
                modifier = Modifier
                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
                    .fillMaxWidth()
                    .scrollEnabled(motionProgress == 0f),
            )

//            MediaColumn(
//                playlist = musicState.playlist,
//                listState = listState,
//                viewType = viewType,
//                onItemClick = {
//                    if (swipeableState.currentValue == 0) {
//                        if (musicState.currentPlayingMusic != it) {
//                            onEvent(MusicPlayerScreenEvent.OnPlayClick(it))
//                        }
//                        scope.launch {
//                            swipeableState.animateTo(1)
//                        }
//                    }
//                },
//                onDropDownMenuClick = { index, song ->
//                    val event = MediaDropDownMenuEvent.fromIndex(index, song)
//                    onDropDownMenuEvent(event)
//                },
//                modifier = Modifier
//                    .alpha(max(1f - Float.min(motionProgress * 2, 1f), 0.7f))
//                    .fillMaxWidth()
//                    .scrollEnabled(motionProgress == 0f),
//            )
        }
    )
    MusicOptionDialog(
        playlistType = musicPlayerScreenState.musicState.playlistType,
        openDialog = openDialog,
        onDismiss = {
            openDialog = false
        },
        onOkButtonClicked = {
            openDialog = false
            onEvent(MusicPlayerScreenEvent.OnPlaylistTypeChanged(it))
        }
    )
}

@Composable
private fun SongMediaColumn(
    musicState: MusicState,
    listState: LazyListState,
    songMediaColumnItemType: Boolean,

    onSongClick: (song: Song) -> Unit,
    onMediaItemEvent: (MusicMediaItemEvent) -> Unit,

    modifier: Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(
            items = musicState.playlist,
            key = { song: Song -> song.hashCode() }
        ) { song ->
            SongMediaColumnItem(
                song = song,
                viewType = songMediaColumnItemType,
                onMediaItemClick = { onSongClick(song) },
                onDropDownMenuClick = {
                    val event = MusicDropDownMenuState.indexToEvent(it, song)
                    onMediaItemEvent(event)
                }
            )
        }
    }
}

@Composable
private fun SongMediaColumnItem(
    song: Song,
    viewType: Boolean,

    onMediaItemClick: () -> Unit,
    onDropDownMenuClick: (Int) -> Unit,
) {
    if(viewType) {
        MediaItemLarge(
            title = song.title,
            subTitle = song.artist,
            imageUrl = song.imageUrl,
            onItemClick = { onMediaItemClick() },
            onDropDownMenuClick = { onDropDownMenuClick(it) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
    } else {
        MediaItemSmall(
            imageUrl = song.imageUrl,
            title = song.title,
            subTitle = song.artist,
            showContextualMenu = true,
            onItemClick = { onMediaItemClick() },
            onDropDownMenuClick = { onDropDownMenuClick(it) },
            modifier = Modifier,
        )
    }
}

@Composable
private fun MusicSongHeader(
    viewType: Boolean,
    onSeeMoreButtonClick: () -> Unit,
    onViewTypeClick: (Boolean) -> Unit,

    modifier: Modifier,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        TextButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onSeeMoreButtonClick,
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
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {
                onViewTypeClick(!viewType)
            },
            content = {
                Icon(
                    imageVector = if(viewType) Icons.Filled.List else Icons.Outlined.Image,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = UiText.StringResource(R.string.option_see_more).asString()
                )
            }
        )
    }
}
@Preview
@Composable
private fun MusicScreenPreview() {
    PreviewTheme(false) {
        MusicSongScreen(
            musicPlayerScreenState = MusicPlayerScreenState.default,
            onEvent = { _, -> },
            onMusicMediaItemEvent = { }
        )
    }
}