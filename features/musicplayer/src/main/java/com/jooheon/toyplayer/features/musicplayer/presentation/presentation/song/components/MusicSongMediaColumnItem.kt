package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.compose.theme.themes.PreviewTheme
import com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown.MusicDropDownMenuState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaItemLarge
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.MediaItemSmall
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.SongItemEvent


@Composable
internal fun MusicSongMediaColumnItem(
    song: Song,
    viewType: Boolean,

    onMediaItemClick: () -> Unit, // FIXME
    onMediaItemEvent: (SongItemEvent) -> Unit,
) {
    if(viewType) {
        MediaItemLarge(
            title = song.title,
            subTitle = song.artist,
            imageUrl = song.imageUrl,
            onItemClick = { onMediaItemClick() },
            onDropDownMenuClick = {
                val event = MusicDropDownMenuState.indexToEvent(it, song)
                onMediaItemEvent(event)
            },
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
            onDropDownMenuClick = {
                val event = MusicDropDownMenuState.indexToEvent(it, song)
                onMediaItemEvent(event)
            },
            modifier = Modifier,
        )
    }
}

@Preview
@Composable
private fun MusicSongMediaColumnItemPreview() {
    PreviewTheme(true) {
        MusicSongMediaColumnItem(
            song = Song.default,
            viewType = true,
            onMediaItemEvent = {},
            onMediaItemClick = {},
        )
    }
}

@Preview
@Composable
private fun MusicSongMediaColumnItemPreview2() {
    PreviewTheme(true) {
        MusicSongMediaColumnItem(
            song = Song.default,
            viewType = false,
            onMediaItemEvent = {},
            onMediaItemClick = {},
        )
    }
}