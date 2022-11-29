package com.jooheon.clean_architecture.presentation.service.music.extensions

import com.jooheon.clean_architecture.domain.entity.Entity


enum class MusicState {
    PLAYING, PAUSED, NONE
}

data class MusicScreenState(
    val songList: List<Entity.Song> = emptyList(),
    val currentPlayingMusic: Entity.Song? = null,
    val searchBarText: String = "",
    val musicState: MusicState = MusicState.NONE
) {
    val isMusicBottomBarVisible =
        currentPlayingMusic != null && (musicState == MusicState.PLAYING || musicState == MusicState.PAUSED)

    val isMusicPlaying = musicState == MusicState.PLAYING
}
