package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

sealed class SongItemEvent {
    data object Placeholder: SongItemEvent()
    data class OnAddToPlayingQueueClick(val song: Song): SongItemEvent()
    data class OnAddPlaylistClick(val song: Song, val playlist: Playlist?): SongItemEvent()
    data class OnTagEditorClick(val song: Song): SongItemEvent()
    data class OnDetailsClick(val song: Song): SongItemEvent()
}