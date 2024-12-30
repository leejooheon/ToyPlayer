package com.jooheon.toyplayer.features.musicplayer.presentation.library.playingqueue.model

sealed class MusicPlayingQueueScreenEvent {
    data object OnBackClick: MusicPlayingQueueScreenEvent()
}