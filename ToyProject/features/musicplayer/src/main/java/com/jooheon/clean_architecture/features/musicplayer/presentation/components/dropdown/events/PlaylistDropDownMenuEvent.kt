package com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events

import com.jooheon.clean_architecture.domain.entity.music.Playlist

sealed class PlaylistDropDownMenuEvent {
    object Placeholder: PlaylistDropDownMenuEvent()
    data class OnPlaylistDelete(val playlist: Playlist): PlaylistDropDownMenuEvent()
    data class OnPlaylistNameChange(val playlist: Playlist): PlaylistDropDownMenuEvent()
    data class OnPlaylistSaveAsFile(val playlist: Playlist): PlaylistDropDownMenuEvent()

    companion object {
        fun fromIndex(index: Int, playlist: Playlist) = when (index) {
            0 -> OnPlaylistDelete(playlist)
            1 -> OnPlaylistNameChange(playlist)
            2 -> OnPlaylistSaveAsFile(playlist)
            else -> Placeholder
        }
    }
}