package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist

sealed class MusicPlaylistItemEvent {
    object Placeholder: MusicPlaylistItemEvent()
    data class OnDelete(val playlist: Playlist): MusicPlaylistItemEvent()
    data class OnChangeName(val playlist: Playlist): MusicPlaylistItemEvent()
    data class OnSaveAsFile(val playlist: Playlist): MusicPlaylistItemEvent()
}