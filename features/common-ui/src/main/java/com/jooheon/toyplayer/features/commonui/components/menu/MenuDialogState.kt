package com.jooheon.toyplayer.features.commonui.components.menu

import com.jooheon.toyplayer.domain.model.music.Song

data class MenuDialogState(
    val type: Type,
    val song: Song,
) {
    enum class Type {
        None, SongInfo, SelectPlaylist, NewPlaylist
    }
    companion object {
        val default = MenuDialogState(
            type = Type.None,
            song = Song.default,
        )
    }
}