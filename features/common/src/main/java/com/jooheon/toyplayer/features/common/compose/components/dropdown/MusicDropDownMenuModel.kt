package com.jooheon.toyplayer.features.common.compose.components.dropdown

import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText

enum class MusicDropDownMenu(val title: UiText) {
    PlaylistChangeName(UiText.StringResource(Strings.action_change_name)),
    PlaylistDelete(UiText.StringResource(Strings.action_delete)),

    PlaylistMediaItemDelete(UiText.StringResource(Strings.action_delete)),
    PlaylistMediaItemDetails(UiText.StringResource(Strings.action_details)),

    MediaItemAddToPlayingQueue(UiText.StringResource(Strings.action_add_to_playing_queue)),
    MediaItemAddToPlaylist(UiText.StringResource(Strings.action_add_playlist)),
    MediaItemDetails(UiText.StringResource(Strings.action_details)),
    ;

    companion object {
        val playlistMenuItems = listOf(PlaylistDelete, PlaylistChangeName)
        val playlistMediaItemMenuItems = listOf(PlaylistMediaItemDelete, PlaylistMediaItemDetails)
        val mediaMenuItems = listOf(MediaItemAddToPlayingQueue, MediaItemAddToPlaylist, MediaItemDetails)
    }
}