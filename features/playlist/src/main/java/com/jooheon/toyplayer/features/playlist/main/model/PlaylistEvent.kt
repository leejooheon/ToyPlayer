package com.jooheon.toyplayer.features.playlist.main.model

sealed interface PlaylistEvent {
    data class OnAddPlaylist(val name: String, val id: Int): PlaylistEvent
    data class OnDeletePlaylist(val id: Int): PlaylistEvent
    data class OnPlaylistClick(val id: Int): PlaylistEvent
}