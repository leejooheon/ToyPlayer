package com.jooheon.toyplayer.features.musicplayer.presentation.common.dropdown

import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.essential.base.UiText
import com.jooheon.toyplayer.features.musicplayer.R
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.PlaylistEvent

data class MusicDropDownMenuState(
    val items: List<UiText>,
) {
    companion object {
        val mediaItems = listOf(
            UiText.StringResource(R.string.action_add_to_playing_queue),
            UiText.StringResource(R.string.action_add_playlist),
            UiText.StringResource(R.string.action_tag_editor),
            UiText.StringResource(R.string.action_details),
            UiText.StringResource(R.string.action_delete),
        )

        val playlistItems = listOf(
            UiText.StringResource(R.string.action_delete),
            UiText.StringResource(R.string.action_change_name),
            UiText.StringResource(R.string.action_save_as_file),
        )

        val playlistMediaItems = listOf(
            UiText.StringResource(R.string.action_delete),
            UiText.StringResource(R.string.action_details),
        )

        val albumSortItems = listOf(
            UiText.StringResource(R.string.action_album_sort_by_album_name),
            UiText.StringResource(R.string.action_album_sort_by_artist_name)
        )

        val artistSortItems = listOf(
            UiText.StringResource(R.string.action_arist_sort_by_artist_name),
            UiText.StringResource(R.string.action_arist_sort_by_number_of_song),
            UiText.StringResource(R.string.action_arist_sort_by_number_of_album),
        )

        fun indexToEvent(
            index: Int,
            song: Song,
            playlist: Playlist? = null,
        ): SongItemEvent {
            return when(index) {
                0 -> SongItemEvent.OnAddToPlayingQueueClick(song)
                1 -> SongItemEvent.OnAddPlaylistClick(song, playlist)
                2 -> SongItemEvent.OnTagEditorClick(song)
                3 -> SongItemEvent.OnDetailsClick(song)
                else -> SongItemEvent.Placeholder
            }
        }

        fun indexToEvent(
            index: Int,
            playlist: Playlist,
        ): PlaylistEvent {
            return when(index) {
                0 -> PlaylistEvent.OnDelete(playlist)
                1 -> PlaylistEvent.OnChangeName(playlist)
                2 -> PlaylistEvent.OnSaveAsFile(playlist)
                else -> PlaylistEvent.Placeholder
            }
        }
    }
}