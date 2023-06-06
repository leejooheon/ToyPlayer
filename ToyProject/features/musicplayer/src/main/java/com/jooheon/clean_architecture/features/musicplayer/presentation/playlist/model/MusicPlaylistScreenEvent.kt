package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist

sealed class MusicPlaylistScreenEvent {
    object Refresh: MusicPlaylistScreenEvent()
    data class OnPlaylistClick(val playlist: Playlist): MusicPlaylistScreenEvent()
    data class OnAddPlaylist(val title: String): MusicPlaylistScreenEvent()
}