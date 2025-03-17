package com.jooheon.toyplayer.features.player.model

sealed interface PlayerEvent {
    data object OnPlayPauseClick: PlayerEvent
    data object OnNextClick: PlayerEvent
    data object OnPreviousClick: PlayerEvent
    data object OnSettingClick: PlayerEvent
    data object OnPlaylistClick: PlayerEvent
    data object OnLibraryClick: PlayerEvent

    data class OnScreenTouched(val state: Boolean): PlayerEvent
    data object OnPlayAutomatic: PlayerEvent

    data class OnContentClick(val playlistId: Int, val index: Int): PlayerEvent
    data class OnSwipe(val index: Int): PlayerEvent
}