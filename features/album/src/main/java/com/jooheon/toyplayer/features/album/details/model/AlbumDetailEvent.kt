package com.jooheon.toyplayer.features.album.details.model

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

sealed interface AlbumDetailEvent {
    data class OnPlayAllClick(val shuffle: Boolean): AlbumDetailEvent
    data class OnSongClick(val index: Int): AlbumDetailEvent
    data class OnAddPlayingQueue(val song: Song): AlbumDetailEvent
    data class OnAddPlaylist(val playlist: Playlist, val song: Song): AlbumDetailEvent
}