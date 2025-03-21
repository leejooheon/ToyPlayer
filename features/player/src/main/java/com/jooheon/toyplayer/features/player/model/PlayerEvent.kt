package com.jooheon.toyplayer.features.player.model

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

sealed interface PlayerEvent {
    data object OnPlayPauseClick: PlayerEvent
    data object OnNextClick: PlayerEvent
    data object OnPreviousClick: PlayerEvent

    data object OnNavigateSettingClick: PlayerEvent
    data object OnNavigatePlaylistClick: PlayerEvent
    data object OnNavigateLibraryClick: PlayerEvent

    data class OnScreenTouched(val state: Boolean): PlayerEvent
    data object OnPlayAutomatic: PlayerEvent

    data class OnFavoriteClick(val song: Song): PlayerEvent
    data class OnPlaylistClick(val playlist: Playlist, val index: Int): PlayerEvent
    data class OnSwipe(val index: Int): PlayerEvent
}