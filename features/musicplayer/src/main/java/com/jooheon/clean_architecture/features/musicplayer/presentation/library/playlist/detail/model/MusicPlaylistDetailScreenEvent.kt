package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicPlaylistDetailScreenEvent {
    object OnBackClick: MusicPlaylistDetailScreenEvent()
    data class OnSongClick(val song: Song): MusicPlaylistDetailScreenEvent()
}