package com.jooheon.clean_architecture.features.musicservice.ext

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.toyproject.features.common.utils.MusicUtil


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