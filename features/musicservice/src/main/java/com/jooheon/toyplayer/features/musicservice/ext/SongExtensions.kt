package com.jooheon.toyplayer.features.musicservice.ext

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_ALBUM_ID
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_ARTIST_ID
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_DATA
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_DURATION
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_IS_FAVORITE
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_PATH
import com.jooheon.toyplayer.domain.entity.music.Song.Companion.BUNDLE_USE_CACHE
import com.jooheon.toyplayer.features.common.utils.MusicUtil



val Song.uri get() = MusicUtil.getSongFileUri(path)
val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(this.imageUrl)
fun Song.extras() = bundleOf(
    BUNDLE_USE_CACHE to useCache,
    BUNDLE_ARTIST_ID to artistId,
    BUNDLE_ALBUM_ID to albumId,
    BUNDLE_DURATION to duration,
    BUNDLE_IS_FAVORITE to isFavorite,
    BUNDLE_DATA to data,
    BUNDLE_PATH to path,
)