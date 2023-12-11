package com.jooheon.clean_architecture.features.musicservice.data

import android.media.session.PlaybackState
import android.os.Parcelable
import androidx.media3.common.C
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicState(
    val currentPlayingMusic: Song = Song.default,
    val playbackState: Int = PlaybackState.STATE_NONE,
    val timePassed: Long = C.TIME_UNSET
): Parcelable