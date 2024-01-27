package com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.toyplayer.domain.entity.music.Playlist

sealed class PlaylistEvent {
    object Placeholder: PlaylistEvent()
    data class OnDelete(val playlist: Playlist): PlaylistEvent()
    data class OnChangeName(val playlist: Playlist): PlaylistEvent()
    data class OnSaveAsFile(val playlist: Playlist): PlaylistEvent()
}