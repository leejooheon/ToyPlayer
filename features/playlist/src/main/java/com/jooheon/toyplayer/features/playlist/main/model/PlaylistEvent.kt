package com.jooheon.toyplayer.features.playlist.main.model

import com.jooheon.toyplayer.domain.model.music.Playlist

sealed interface PlaylistEvent {
    data class OnAddPlaylist(val title: String): PlaylistEvent
    data class OnPlaylistClick(val playlist: Playlist): PlaylistEvent
    data class OnDropDownMenuClick(val index: Int, val playlist: Playlist): PlaylistEvent
}