package com.jooheon.clean_architecture.presentation.service.music.extensions

import android.os.Parcelable
import com.jooheon.clean_architecture.domain.entity.Entity
import kotlinx.parcelize.Parcelize


enum class PlaybackMode {
    REPEAT_ONE,
    REPEAT_ALL,
    REPEAT_OFF
}

@Parcelize
data class MusicState(
    val songs: List<Entity.Song> =  emptyList(),
    val currentSongQueue: List<Entity.Song> = emptyList(),
    val currentPlayingMusic: Entity.Song = Entity.Song.emptySong,
    val currentDuration: Long = 0,
    val isPlaying: Boolean = false,
    val isShuffled: Boolean = false,
    val isMusicBottomBarVisible: Boolean = true,
    val playbackMode: PlaybackMode = PlaybackMode.REPEAT_OFF,
//    val skipForwardBackward: SkipForwardBackward = SkipForwardBackward.FIVE_SECOND
): Parcelable
