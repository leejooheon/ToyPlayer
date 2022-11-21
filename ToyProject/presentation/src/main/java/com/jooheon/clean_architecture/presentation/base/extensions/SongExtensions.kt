package com.jooheon.clean_architecture.presentation.base.extensions

import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.utils.MusicUtil

val Entity.Song.uri get() = MusicUtil.getSongFileUri(songId = id)
val Entity.Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(albumId)