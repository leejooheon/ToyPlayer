package com.jooheon.clean_architecture.features.musicservice.ext

import android.support.v4.media.MediaMetadataCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.toyproject.features.common.utils.MusicUtil

val Player.currentWindow: Timeline.Window?
    get() = if (mediaItemCount == 0) null else currentTimeline.getWindow(currentMediaItemIndex, Timeline.Window())
val Timeline.mediaItems: List<MediaItem>
    get() = List(windowCount) {
        getWindow(it, Timeline.Window()).mediaItem
    }
inline val Timeline.windows: List<Timeline.Window>
    get() = List(windowCount) {
        getWindow(it, Timeline.Window())
    }

val Song.uri get() = MusicUtil.getSongFileUri(path)
val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(this.imageUrl)
fun Song.toMediaItem() = MediaItem.Builder()
    .setUri(uri)
    .setMediaId(id())
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setDisplayTitle(displayName)
            .setTitle(title)
            .setAlbumTitle(album)
            .setAlbumArtist(artist)
            .setArtist(artist)
            .setArtworkUri(albumArtUri)
            .build()
    ).build()