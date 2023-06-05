package com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.MediaDropDownMenuEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.components.dropdown.events.PlaylistDropDownMenuEvent

data class MusicDropDownMenuState(
    val items: List<UiText>,
) {
    companion object {
        val mediaItems = listOf(
            UiText.StringResource(R.string.action_add_to_playing_queue),
            UiText.StringResource(R.string.action_add_playlist),
            UiText.StringResource(R.string.action_tag_editor),
            UiText.StringResource(R.string.action_details),
        )

        val playlistItems = listOf(
            UiText.StringResource(R.string.action_delete),
            UiText.StringResource(R.string.action_change_name),
            UiText.StringResource(R.string.action_save_as_file),
        )
    }
}