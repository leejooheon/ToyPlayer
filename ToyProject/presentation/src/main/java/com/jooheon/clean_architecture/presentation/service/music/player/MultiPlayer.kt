package com.jooheon.clean_architecture.presentation.service.music.player

import android.content.Context
import android.media.MediaPlayer
import android.os.PowerManager
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.extensions.uri
import com.jooheon.clean_architecture.presentation.service.music.playback.Playback

class MultiPlayer(context: Context) : LocalPlayback(context) {
    private var mCurrentMediaPlayer = MediaPlayer()
    override var callbacks: Playback.PlaybackCallbacks? = null

    override var isInitialized = false // @return True if the player is ready to go, false otherwise
        private set

    override val isPlaying: Boolean // Checks whether the MultiPlayer is playing.
        get() = isInitialized && mCurrentMediaPlayer.isPlaying

    override val audioSessionId: Int // @return The current audio session ID.
        get() = mCurrentMediaPlayer.audioSessionId

    init {
        mCurrentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
    }

    override fun setDataSource(
        song: Entity.Song,
        force: Boolean,
        completion: (success: Boolean) -> Unit
    ) {
        isInitialized = false
        setDataSourceImpl(mCurrentMediaPlayer, song.uri.toString()) { success ->
            isInitialized = success
            if (isInitialized) {
                setNextDataSource(null)
            }
            completion(isInitialized)
        }
    }

    override fun start(): Boolean {
        super.start()
        return try {
            mCurrentMediaPlayer.start()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun setNextDataSource(path: String?) {
        // nothing yet
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    override fun pause(): Boolean {
        TODO("Not yet implemented")
    }

    override fun duration(): Int {
        TODO("Not yet implemented")
    }

    override fun position(): Int {
        TODO("Not yet implemented")
    }

    override fun seek(whereto: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setVolume(vol: Float): Boolean {
        TODO("Not yet implemented")
    }

    override fun setAudioSessionId(sessionId: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun setCrossFadeDuration(duration: Int) {
        TODO("Not yet implemented")
    }

    override fun setPlaybackSpeedPitch(speed: Float, pitch: Float) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(p0: MediaPlayer?) {
        TODO("Not yet implemented")
    }
}