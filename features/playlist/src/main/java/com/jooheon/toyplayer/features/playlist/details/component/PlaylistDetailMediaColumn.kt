package com.jooheon.toyplayer.features.playlist.details.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.core.designsystem.theme.ToyPlayerTheme
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.commonui.components.menu.DropDownMenu
import com.jooheon.toyplayer.features.commonui.components.media.MediaDetailHeader
import com.jooheon.toyplayer.features.commonui.components.media.MediaItemSmallNoImage

@Composable
internal fun PlaylistDetailMediaColumn(
    listState: LazyListState,
    playlist: Playlist,
    onPlayClick: (index: Int) -> Unit,
    onPlayAllClick: (shuffle: Boolean) -> Unit,
    onDropDownEvent: (DropDownMenu, Song) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            item {
                PlaylistDetailHeader(
                    playlist = playlist,
                    onPlayAllClick = { onPlayAllClick(it) },
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
                    index = index + 1,
                    title = song.title,
                    subTitle = "${song.artist} • ${song.album}",
                    duration = MusicUtil.toReadableDurationString(song.duration),
                    dropDownMenus = DropDownMenu.playlistMediaItemMenuItems,
                    onItemClick = { onPlayClick(index) },
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
    ToyPlayerTheme {
        PlaylistDetailMediaColumn(
            listState = rememberLazyListState(),
            playlist = Playlist.default,
            onPlayClick = {},
            onPlayAllClick = {},
            onDropDownEvent = { _, _ -> },
        )
    }
}
