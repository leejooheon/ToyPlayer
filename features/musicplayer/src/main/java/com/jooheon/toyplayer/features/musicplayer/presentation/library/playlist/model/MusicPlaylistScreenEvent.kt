package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.model

import com.jooheon.toyplayer.domain.entity.music.Playlist

sealed class MusicPlaylistScreenEvent {
    object Refresh: MusicPlaylistScreenEvent()
    data class OnPlaylistClick(val playlist: Playlist): MusicPlaylistScreenEvent()
    data class OnAddPlaylist(val title: String): MusicPlaylistScreenEvent()
}