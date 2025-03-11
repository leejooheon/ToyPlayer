package com.jooheon.toyplayer.features.common.compose.components.media

import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

data class MusicDropDownMenuState(
    val items: List<UiText>,
) {
    companion object {
        val mediaItems = listOf(
            UiText.StringResource(Strings.action_add_to_playing_queue),
            UiText.StringResource(Strings.action_add_playlist),
            UiText.StringResource(Strings.action_tag_editor),
            UiText.StringResource(Strings.action_details),
            UiText.StringResource(Strings.action_delete),
        )

        val playlistItems = listOf(
            UiText.StringResource(Strings.action_delete),
            UiText.StringResource(Strings.action_change_name),
            UiText.StringResource(Strings.action_save_as_file),
        )

        val playlistMediaItems = listOf(
            UiText.StringResource(Strings.action_delete),
            UiText.StringResource(Strings.action_details),
        )

        val albumSortItems = listOf(
            UiText.StringResource(Strings.action_album_sort_by_album_name),
            UiText.StringResource(Strings.action_album_sort_by_artist_name)
        )

        val artistSortItems = listOf(
            UiText.StringResource(Strings.action_arist_sort_by_artist_name),
            UiText.StringResource(Strings.action_arist_sort_by_number_of_song),
            UiText.StringResource(Strings.action_arist_sort_by_number_of_album),
        )

//        fun indexToEvent(
//            index: Int,
//            song: Song,
//            playlist: Playlist? = null,
//        ): SongItemEvent {
//            return when(index) {
//                0 -> SongItemEvent.OnAddToPlayingQueueClick(song)
//                1 -> SongItemEvent.OnAddPlaylistClick(song, playlist)
//                2 -> SongItemEvent.OnTagEditorClick(song)
//                3 -> SongItemEvent.OnDetailsClick(song)
//                else -> SongItemEvent.Placeholder
//            }
//        }
//
//        fun indexToEvent(
//            index: Int,
//            playlist: Playlist,
//        ): PlaylistEvent {
//            return when(index) {
//                0 -> PlaylistEvent.OnDelete(playlist)
//                1 -> PlaylistEvent.OnChangeName(playlist)
//                2 -> PlaylistEvent.OnSaveAsFile(playlist)
//                else -> PlaylistEvent.Placeholder
//            }
//        }
    }
}