package com.jooheon.toyplayer.features.player.model

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.musicservice.data.MusicState

data class PlayerUiState(
    val musicState: MusicState,
    val pagerModel: PagerModel,
    val contentModels: List<ContentModel>,
    private val isLoading: Boolean,
) {
    fun isLoading(): Boolean {
        return isLoading || musicState.isLoading()
    }

    data class PagerModel(
        val items: List<Song>,
        val currentPlaylist: Playlist, // FIXME: MusicState로 바꾸자
    ) {
        fun currentPageIndex(currentSongKey: String): Int {
            val index = items
                .indexOfFirst { it.key() == currentSongKey }
                .takeIf { it != -1 }
                .defaultZero()

            return index
        }
        companion object {
            val default = PagerModel(
                items = emptyList(),
                currentPlaylist = Playlist.default,
            )
            val preview = PagerModel(
                items = listOf(Song.preview, Song.preview.copy(audioId = 2), Song.preview.copy(audioId = 3)),
                currentPlaylist = Playlist.preview,
            )
        }
    }

    data class ContentModel(
        val playlist: Playlist,
        val requirePermission: Boolean,
    ) {
        companion object {
            val default = ContentModel(
                playlist = Playlist.default,
                requirePermission = false,
            )
            val preview = ContentModel(
                playlist = Playlist.preview,
                requirePermission = false,
            )
        }
    }

    companion object {
        val preview = PlayerUiState(
            musicState = MusicState.preview,
            pagerModel = PagerModel.preview,
            contentModels = listOf(ContentModel.preview, ContentModel.preview),
            isLoading = true,
        )
        val default = PlayerUiState(
            musicState = MusicState(),
            pagerModel = PagerModel.default,
            contentModels = emptyList(),
            isLoading = false,
        )
    }

}

fun List<PlayerUiState.ContentModel>.toChunkedModel(): List<List<PlayerUiState. ContentModel>> {
    return chunked(4).let { chunks ->
        if (chunks.lastOrNull().defaultEmpty().size < 4 && chunks.size > 1) {
            val lastChunk = chunks.last()
            val previousChunk = chunks[chunks.size - 2]
            val mergedChunk = previousChunk + lastChunk

            val mutableChunks = chunks.toMutableList()
            mutableChunks[mutableChunks.size - 2] = mergedChunk
            mutableChunks.removeAt(mutableChunks.lastIndex)

            mutableChunks
        } else {
            chunks
        }
    }
}