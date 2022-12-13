package com.jooheon.clean_architecture.presentation.service.music.extensions

import android.os.Parcelable
import com.jooheon.clean_architecture.domain.entity.Entity
import kotlinx.parcelize.Parcelize


enum class RepeatMode {
    REPEAT_ONE,
    REPEAT_ALL,
    REPEAT_OFF
}

enum class ShuffleMode {
    SHUFFLE,
    NONE,
}


@Parcelize
data class MusicState(
    val songs: List<Entity.Song> =  emptyList(),
    val currentSongQueue: List<Entity.Song> = emptyList(),
    val currentPlayingMusic: Entity.Song = Entity.Song.emptySong,
    val isPlaying: Boolean = false,
    val isShuffled: Boolean = false,
    val isMusicBottomBarVisible: Boolean = true,
    val repeatMode: RepeatMode = RepeatMode.REPEAT_OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.NONE
//    val skipForwardBackward: SkipForwardBackward = SkipForwardBackward.FIVE_SECOND
): Parcelable
