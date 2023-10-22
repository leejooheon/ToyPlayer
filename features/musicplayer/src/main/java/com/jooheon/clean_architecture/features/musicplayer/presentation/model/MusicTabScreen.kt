package com.jooheon.clean_architecture.features.musicplayer.presentation.model

import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R

enum class MusicTabScreen(val value: UiText) {
    Song(UiText.StringResource(R.string.tab_name_song)),
    Album(UiText.StringResource(R.string.tab_name_album)),
    Artist(UiText.StringResource(R.string.tab_name_artist)),
    Playlist(UiText.StringResource(R.string.tab_name_playlist));

    companion object {
        fun fromIndex(index: Int): MusicTabScreen {
            return entries.getOrNull(index) ?: throw IllegalStateException()
        }
    }
}