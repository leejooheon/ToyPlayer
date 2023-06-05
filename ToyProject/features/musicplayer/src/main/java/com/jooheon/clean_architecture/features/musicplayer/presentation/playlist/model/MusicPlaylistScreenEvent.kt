package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist

sealed class MusicPlaylistScreenEvent {
    data class onPlaylistClick(val playlist: Playlist): MusicPlaylistScreenEvent()
    data class onAddPlaylist(val title: String): MusicPlaylistScreenEvent()
}