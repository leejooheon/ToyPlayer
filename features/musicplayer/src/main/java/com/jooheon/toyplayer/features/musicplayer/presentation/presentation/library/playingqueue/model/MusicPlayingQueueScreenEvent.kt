package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model

sealed class MusicPlayingQueueScreenEvent {
    data object OnBackClick: MusicPlayingQueueScreenEvent()
}