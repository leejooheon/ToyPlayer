package com.jooheon.clean_architecture.features.musicservice.data

import android.os.Parcelable
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicState(
    val playlist: List<Song> = emptyList(),
    val currentPlayingMusic: Song = Song.default,
    val isPlaying: Boolean = false,
    val isShuffled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.REPEAT_OFF,
    val shuffleMode: ShuffleMode = ShuffleMode.NONE,
): Parcelable