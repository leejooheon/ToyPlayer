package com.jooheon.toyplayer.features.player.model

import androidx.compose.runtime.Composable
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.musicservice.data.MusicState
import com.jooheon.toyplayer.features.player.common.contentSize

data class PlayerUiState(
    val musicState: MusicState,
    val pagerModel: PagerModel,
    val playlists: List<Playlist>,
    private val isLoading: Boolean,
) {
    fun isLoading(): Boolean {
        return isLoading || musicState.isLoading()
    }

    data class PagerModel(
        val items: List<Song>,
        val playedName: String,
        val playedThumbnailImage: String,
    ) {
        fun currentPageIndex(audioId: Long): Int {
            val index = items
                .indexOfFirst { it.audioId == audioId }
                .takeIf { it != -1 }
                .defaultZero()

            return index
        }
        companion object {
            val default = PagerModel(
                items = emptyList(),
                playedName = "",
                playedThumbnailImage = "",
            )
            val preview = PagerModel(
                items = listOf(Song.preview, Song.preview.copy(audioId = 2), Song.preview.copy(audioId = 3)),
                playedName = "preview",
                playedThumbnailImage = "",
            )
        }
    }

    companion object {
        val preview = PlayerUiState(
            musicState = MusicState.preview,
            pagerModel = PagerModel.preview,
            playlists = listOf(Playlist.preview, Playlist.preview),
            isLoading = true,
        )
        val default = PlayerUiState(
            musicState = MusicState.default,
            pagerModel = PagerModel.default,
            playlists = emptyList(),
            isLoading = false,
        )
    }
}


@Composable
fun List<Playlist>.toChunkedModel(): List<List<Playlist>> {
    // Note: 한 화면에 보이는 컨텐츠 갯수 조절방법: chunked 사이즈, contentSpace에서 나누는 부분
    return chunked(contentSize()).let { chunks ->
        if (chunks.lastOrNull().defaultEmpty().size < contentSize() && chunks.size > 1) {
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