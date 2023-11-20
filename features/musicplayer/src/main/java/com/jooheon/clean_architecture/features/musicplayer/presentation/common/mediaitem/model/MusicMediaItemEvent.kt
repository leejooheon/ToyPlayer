package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicMediaItemEvent {
    object Placeholder: MusicMediaItemEvent()
    data class OnAddToPlayingQueueClick(val song: Song): MusicMediaItemEvent()
    data class OnAddPlaylistClick(val song: Song, val playlist: Playlist?): MusicMediaItemEvent()
    data class OnTagEditorClick(val song: Song): MusicMediaItemEvent()
    data class OnDetailsClick(val song: Song): MusicMediaItemEvent()
}