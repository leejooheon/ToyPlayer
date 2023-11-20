package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicArtistDetailScreenEvent {
    object OnBackClick: MusicArtistDetailScreenEvent()
    data class OnSongClick(val song: Song): MusicArtistDetailScreenEvent()
    data class OnAlbumClick(val album: Album): MusicArtistDetailScreenEvent()
}