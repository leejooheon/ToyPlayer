package com.jooheon.toyplayer.features.musicservice.player

import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.castapi.CastController
import com.jooheon.toyplayer.domain.castapi.CastStateHolder

@UnstableApi
class TestPlayer(
    player: Player,
    private val castController: CastController,
    private val castStateHolder: CastStateHolder,
): ForwardingPlayer(player) {
    override fun play() {
        addListener()
        val remoteUrl = "https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3"
//        castController.play(
//            uri = remoteUrl,
//            seekTo = 0L
//        )
        castController.pause()
    }

    override fun isPlaying(): Boolean {
        val state = castStateHolder.state.value
        return state.isPlaying
    }

    override fun getPlaybackState(): Int {
        val state = castStateHolder.state.value
        return state.toPlaybackState()
    }

    override fun isLoading(): Boolean {
        val state = castStateHolder.state.value
        return state.isBuffering
    }

    override fun pause() {
        castController.pause()
    }
    override fun stop() {
        castController.stop()
    }
    override fun seekTo(positionMs: Long) {
        castController.seekTo(positionMs)
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        castController.shuffleModeEnabled(shuffleModeEnabled)
    }

    override fun setRepeatMode(repeatMode: Int) {
        castController.repeatMode(repeatMode)
    }

    override fun setMediaItems(mediaItems: List<MediaItem>) {
        super.setMediaItems(mediaItems)
    }

    override fun setMediaItems(mediaItems: List<MediaItem>, resetPosition: Boolean) {
        super.setMediaItems(mediaItems, resetPosition)
    }

    override fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        super.setMediaItems(mediaItems, startIndex, startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        super.setMediaItem(mediaItem)
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        super.setMediaItem(mediaItem, startPositionMs)
    }
}