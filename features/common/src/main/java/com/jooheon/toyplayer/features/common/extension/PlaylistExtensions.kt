package com.jooheon.toyplayer.features.common.extension

import android.content.Context
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist

fun List<Playlist>.withOutDefault(): List<Playlist> = this.filterNot {
    it.id in Playlist.defaultPlaylists.map { it.id }
}

fun Playlist.showFavorite(): Boolean = id !in listOf(
    Playlist.PlayingQueue.id,
    Playlist.Favorite.id
)