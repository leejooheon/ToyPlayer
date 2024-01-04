package com.jooheon.toyplayer.features.musicservice.ext

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.utils.MusicUtil


val Song.uri get() = MusicUtil.getSongFileUri(path)
val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(this.imageUrl)
fun Song.toMediaItem() = MediaItem.Builder()
    .setUri(key())
    .setMediaId(key())
    .setCustomCacheKey(key())
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setDisplayTitle(displayName)
            .setTitle(title)
            .setAlbumTitle(album)
            .setAlbumArtist(artist)
            .setArtist(artist)
            .setArtworkUri(albumArtUri)
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .build()
    ).build()
