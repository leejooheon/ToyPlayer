package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.detail.model

import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicArtistDetailScreenEvent {
    data object OnBackClick: MusicArtistDetailScreenEvent()
    data class OnAlbumClick(val album: Album): MusicArtistDetailScreenEvent()
}