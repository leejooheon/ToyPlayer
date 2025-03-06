package com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.model

import com.jooheon.toyplayer.domain.model.music.Album

sealed class MusicArtistDetailScreenEvent {
    data object OnBackClick: MusicArtistDetailScreenEvent()
    data class OnAlbumClick(val album: com.jooheon.toyplayer.domain.model.music.Album): MusicArtistDetailScreenEvent()
}