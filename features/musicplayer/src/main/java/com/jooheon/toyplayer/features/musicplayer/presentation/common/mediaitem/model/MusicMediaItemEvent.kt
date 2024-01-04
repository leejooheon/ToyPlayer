package com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicMediaItemEvent {
    data object Placeholder: MusicMediaItemEvent()
    data class OnAddToPlayingQueueClick(val song: Song): MusicMediaItemEvent()
    data class OnAddPlaylistClick(val song: Song, val playlist: Playlist?): MusicMediaItemEvent()
    data class OnTagEditorClick(val song: Song): MusicMediaItemEvent()
    data class OnDetailsClick(val song: Song): MusicMediaItemEvent()
}