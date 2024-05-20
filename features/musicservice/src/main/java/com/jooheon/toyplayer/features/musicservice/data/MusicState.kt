package com.jooheon.toyplayer.features.musicservice.data

import android.media.session.PlaybackState
import androidx.media3.common.C
import com.jooheon.toyplayer.domain.entity.music.Song
import kotlinx.serialization.Serializable

@Serializable
data class MusicState(
    val currentPlayingMusic: Song = Song.default,
    val playbackState: Int = PlaybackState.STATE_NONE,
    val timePassed: Long = C.TIME_UNSET
)