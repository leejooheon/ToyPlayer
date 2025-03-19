package com.jooheon.toyplayer.features.artist.details.model

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

sealed interface ArtistDetailEvent {
    data class OnSongClick(val song: Song): ArtistDetailEvent
    data class OnAddPlayingQueue(val song: Song): ArtistDetailEvent
    data class OnAddPlaylist(val playlist: Playlist, val song: Song): ArtistDetailEvent

    data class OnAlbumClick(val id: String): ArtistDetailEvent
}