package com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events

import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MediaDropDownMenuEvent {
    object Placeholder: MediaDropDownMenuEvent()

    data class OnAddToPlayingQueueClick(val song: Song): MediaDropDownMenuEvent()
    data class OnAddPlaylistClick(val song: Song): MediaDropDownMenuEvent()
    data class OnTagEditorClick(val song: Song): MediaDropDownMenuEvent()
    data class OnDetailsClick(val song: Song): MediaDropDownMenuEvent()

    companion object {
        fun fromIndex(index: Int, song: Song) = when (index) {
            0 -> OnAddToPlayingQueueClick(song)
            1 -> OnAddPlaylistClick(song)
            2 -> OnTagEditorClick(song)
            3 -> OnDetailsClick(song)
            else -> Placeholder
        }
    }
}