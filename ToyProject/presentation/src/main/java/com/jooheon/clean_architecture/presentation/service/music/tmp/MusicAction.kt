package com.jooheon.clean_architecture.presentation.service.music.tmp

import com.jooheon.clean_architecture.domain.entity.Entity

sealed interface MusicAction {
    data class Play(val song: Entity.Song): MusicAction
    data class SnapTo(val duration: Long): MusicAction
    data class PlayAll(val songs: List<Entity.Song>): MusicAction
    data class UpdateSong(val song: Entity.Song): MusicAction
    data class SetPlaying(val isPlaying: Boolean): MusicAction
    data class SetShuffle(val isShuffled: Boolean): MusicAction
    data class UpdateQueueSong(val songs: List<Entity.Song>): MusicAction
    data class CheckScannedSong(val songs: List<Entity.Song>): MusicAction
    data class SetShowBottomMusicPlayer(val isShowed: Boolean): MusicAction
    object PlayLastSongPlayed: MusicAction
    object ChangePlaybackMode: MusicAction
    object Backward: MusicAction
    object Previous: MusicAction
    object Forward: MusicAction
    object Next: MusicAction
    object Stop: MusicAction
}