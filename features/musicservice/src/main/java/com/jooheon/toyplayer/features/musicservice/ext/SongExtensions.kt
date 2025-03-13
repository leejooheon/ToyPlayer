package com.jooheon.toyplayer.features.musicservice.ext

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_ALBUM_ID
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_ARTIST_ID
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_DATA
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_DURATION
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_IS_FAVORITE
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_PATH
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_USE_CACHE
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.toRadioDataOrNull
import com.jooheon.toyplayer.features.common.utils.MusicUtil

val Song.uri get() = MusicUtil.getSongFileUri(path)
val Song.albumArtUri get() = MusicUtil.getMediaStoreAlbumCoverUri(this.imageUrl)
val Song.isRadio: Boolean get() {
    val data = data.defaultEmpty().toRadioDataOrNull()
    return data != null
}
fun Song.extras() = bundleOf(
    BUNDLE_USE_CACHE to useCache,
    BUNDLE_ARTIST_ID to artistId,
    BUNDLE_ALBUM_ID to albumId,
    BUNDLE_DURATION to duration,
    BUNDLE_IS_FAVORITE to isFavorite,
    BUNDLE_DATA to data,
    BUNDLE_PATH to path,
)

fun MediaItem.toSong(): Song {
    fun getUseCache(extras: Bundle?): Boolean = extras?.getBoolean(BUNDLE_USE_CACHE).defaultFalse()
    fun getArtistId(extras: Bundle?): String = extras?.getString(BUNDLE_ARTIST_ID).defaultEmpty()
    fun getAlbumId(extras: Bundle?): String = extras?.getString(BUNDLE_ALBUM_ID).defaultEmpty()
    fun getDuration(extras: Bundle?): Long = extras?.getLong(BUNDLE_DURATION).defaultZero()
    fun getIsFavorite(extras: Bundle?): Boolean = extras?.getBoolean(BUNDLE_IS_FAVORITE).defaultFalse()
    fun getData(extras: Bundle?): String? = extras?.getString(BUNDLE_DATA)
    fun getPath(extras: Bundle?): String = extras?.getString(BUNDLE_PATH).defaultEmpty()

    val audioId = mediaId.toLongOrNull()

    return with(mediaMetadata) {
        Song(
            audioId = audioId.defaultZero(),
            useCache = getUseCache(extras),
            displayName = displayTitle.toString(),
            title = title.toString(),
            artist = artist.toString(),
            artistId = getArtistId(extras),
            album = albumTitle.toString(),
            albumId = getAlbumId(extras),
            duration = getDuration(extras),
            path = getPath(extras),
            trackNumber = trackNumber ?: C.INDEX_UNSET,
            imageUrl = artworkUri.toString(),
            isFavorite = getIsFavorite(extras),
            data = getData(extras)
        )
    }
}