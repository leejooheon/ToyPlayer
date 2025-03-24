package com.jooheon.toyplayer.features.common.extension

import android.content.Context
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist

fun getDefaultPlaylistName(context: Context, mediaId: MediaId): String {
    val resource =  when(mediaId) {
        MediaId.PlayingQueue -> Strings.playlist_playing_queue
        MediaId.RadioSongs -> Strings.playlist_radio
        MediaId.LocalSongs -> Strings.playlist_local
        MediaId.StreamSongs -> Strings.playlist_stream
        MediaId.AssetSongs -> Strings.playlist_asset
        else -> throw IllegalArgumentException("$mediaId is not default playlist.")
    }

    return UiText.StringResource(resource).asString(context)
}

fun List<Playlist>.withOutDefault(): List<Playlist> = this.filterNot {
    it.id in Playlist.defaultPlaylistIds.map { it.first }
}

