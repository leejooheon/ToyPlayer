package com.jooheon.clean_architecture.presentation.service.music.extensions

import android.os.Parcelable
import com.jooheon.clean_architecture.domain.entity.Entity.*
import kotlinx.parcelize.Parcelize

enum class MusicBottomBarState {
    EXPAND, COLLAPSE;

    companion object {
        fun parse(value: Int): MusicBottomBarState {
            val state = when(value) {
                0 -> COLLAPSE
                1 -> EXPAND
                else -> COLLAPSE
            }
            return state
        }
    }
}

@Parcelize
data class MusicState(
    val songs: List<Song> =  emptyList(),
    val currentSongQueue: List<Song> = emptyList(),
    val currentPlayingMusic: Song = Song.emptySong,
    val isPlaying: Boolean = false,
    val isShuffled: Boolean = false,
    val isMusicBottomBarVisible: Boolean = true,
    val repeatMode: RepeatMode = RepeatMode.REPEAT_OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.NONE,
    val skipForwardBackward: SkipForwardBackward = SkipForwardBackward.FIVE_SECOND
): Parcelable
