package com.jooheon.clean_architecture.features.musicservice.data

import android.support.v4.media.MediaMetadataCompat
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.utils.MusicUtil

val Song.uri get() = MusicUtil.getSongFileUri(path)
val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(this.imageUrl)

fun Int.exoPlayerStateAsString() = when(this) {
    ExoPlayer.STATE_IDLE -> "STATE_IDLE"
    ExoPlayer.STATE_BUFFERING -> "STATE_BUFFERING"
    ExoPlayer.STATE_READY -> "STATE_READY"
    ExoPlayer.STATE_ENDED -> "STATE_ENDED"
    else -> throw IllegalStateException("Unknown playbackState.")
}

// These do not have getters, so create a message for the error.
const val NO_GET = "Property does not have a 'get'"

inline var MediaMetadataCompat.Builder.title: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)
    }

inline var MediaMetadataCompat.Builder.album: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, value)
    }

inline var MediaMetadataCompat.Builder.albumArtUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, value)
    }

inline var MediaMetadataCompat.Builder.artist: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }

inline var MediaMetadataCompat.Builder.duration: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, value)
    }
