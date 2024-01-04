package com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.toyplayer.domain.entity.music.Playlist

sealed class MusicPlaylistItemEvent {
    object Placeholder: MusicPlaylistItemEvent()
    data class OnDelete(val playlist: Playlist): MusicPlaylistItemEvent()
    data class OnChangeName(val playlist: Playlist): MusicPlaylistItemEvent()
    data class OnSaveAsFile(val playlist: Playlist): MusicPlaylistItemEvent()
}