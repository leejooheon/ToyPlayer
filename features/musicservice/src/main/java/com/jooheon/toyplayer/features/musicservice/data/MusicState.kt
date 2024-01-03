package com.jooheon.toyplayer.features.musicservice.data

import android.media.session.PlaybackState
import android.os.Parcelable
import androidx.media3.common.C
import com.jooheon.toyplayer.domain.entity.music.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicState(
    val currentPlayingMusic: Song = Song.default,
    val playbackState: Int = PlaybackState.STATE_NONE,
    val timePassed: Long = C.TIME_UNSET
): Parcelable