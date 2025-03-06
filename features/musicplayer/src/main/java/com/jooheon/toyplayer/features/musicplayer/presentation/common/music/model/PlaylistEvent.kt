package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model

import com.jooheon.toyplayer.domain.model.music.Playlist

sealed class PlaylistEvent {
    object Placeholder: PlaylistEvent()
    data class OnDelete(val playlist: Playlist): PlaylistEvent()
    data class OnChangeName(val playlist: Playlist): PlaylistEvent()
    data class OnSaveAsFile(val playlist: Playlist): PlaylistEvent()
}