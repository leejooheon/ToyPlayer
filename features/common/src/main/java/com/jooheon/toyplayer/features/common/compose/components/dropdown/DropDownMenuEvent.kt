package com.jooheon.toyplayer.features.common.compose.components.dropdown

import com.jooheon.toyplayer.domain.model.music.Playlist

sealed interface DropDownMenuEvent {
    data class OnDelete(val id: Int): DropDownMenuEvent
    data class OnChangeName(val id: Int): DropDownMenuEvent
    data class OnSaveAsFile(val id: Playlist): DropDownMenuEvent
}