package com.jooheon.toyplayer.features.player.model

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

sealed interface PlayerEvent {
    data object OnPlayPauseClick: PlayerEvent
    data object OnSettingClick: PlayerEvent

    data class OnScreenTouched(val state: Boolean): PlayerEvent
    data class OnPlayAutomatic(val playlist: Playlist): PlayerEvent

    data class OnContentClick(val playlistId: Int, val song: Song): PlayerEvent
    data class OnSwipe(val song: Song): PlayerEvent
}