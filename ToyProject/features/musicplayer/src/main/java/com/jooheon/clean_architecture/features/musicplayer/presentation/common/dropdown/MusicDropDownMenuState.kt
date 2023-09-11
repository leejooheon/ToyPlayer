package com.jooheon.clean_architecture.features.musicplayer.presentation.common.dropdown

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.clean_architecture.toyproject.features.musicplayer.R
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicPlaylistItemEvent

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
        ): MusicMediaItemEvent {
            return when(index) {
                0 -> MusicMediaItemEvent.OnAddToPlayingQueueClick(song)
                1 -> MusicMediaItemEvent.OnAddPlaylistClick(song, playlist)
                2 -> MusicMediaItemEvent.OnTagEditorClick(song)
                3 -> MusicMediaItemEvent.OnDetailsClick(song)
                else -> MusicMediaItemEvent.Placeholder
            }
        }

        fun indexToEvent(
            index: Int,
            playlist: Playlist,
        ): MusicPlaylistItemEvent{
            return when(index) {
                0 -> MusicPlaylistItemEvent.OnDelete(playlist)
                1 -> MusicPlaylistItemEvent.OnChangeName(playlist)
                2 -> MusicPlaylistItemEvent.OnSaveAsFile(playlist)
                else -> MusicPlaylistItemEvent.Placeholder
            }
        }
        fun indexToEvent(index: Int): MusicAlbumScreenEvent {
            return when(index) {
                0 -> MusicAlbumScreenEvent.OnSortByAlbumName
                1 -> MusicAlbumScreenEvent.OnSortByArtistName
                else -> MusicAlbumScreenEvent.OnSortByAlbumName
            }
        }
    }
}