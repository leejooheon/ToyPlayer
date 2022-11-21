package com.jooheon.clean_architecture.presentation.service.music.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.common.showToast
import com.jooheon.clean_architecture.presentation.service.music.playback.Playback
import com.jooheon.clean_architecture.presentation.utils.VersionUtils

abstract class LocalPlayback(val context: Context) : Playback, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private val TAG = LocalPlayback::class.java.simpleName
    private val AUDIO_TAG = AudioManager::class.java.simpleName

    private val audioManager: AudioManager? = context.getSystemService()
    private var isPausedByTransientLossOfFocus = false

    private var playbackSpeed = 1f // Fixme: preference로 옮기자
    private var playbackPitch = 1f // Fixme: preference로 옮기자

    /**
     * @param player The [MediaPlayer] to use
     * @param path The path of the file, or the http/rtsp URL of the stream you want to play
     * @return True if the <code>player</code> has been prepared and is ready to play, false otherwise
     */
    fun setDataSourceImpl(
        player: MediaPlayer,
        path: String,
        completion: (success: Boolean) -> Unit,
    ) {
        player.reset()
        try {
            if (path.startsWith("content://")) {
                player.setDataSource(context, path.toUri())
            } else {
                player.setDataSource(path)
            }
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            if (VersionUtils.hasMarshmallow())
                player.playbackParams =
                    PlaybackParams().setSpeed(playbackSpeed).setPitch(playbackPitch)

            player.setOnPreparedListener {
                player.setOnPreparedListener(null)
                completion(true)
            }
            player.prepareAsync()
        } catch (e: Exception) {
            completion(false)
            e.printStackTrace()
        }
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
    }

    @CallSuper
    override fun start(): Boolean {
        if (!requestFocus()) {
            context.showToast(context.getString(R.string.some_error))
        }
//        registerBecomingNoisyReceiver()
        return true
    }

    @CallSuper
    override fun pause(): Boolean {
//        unregisterBecomingNoisyReceiver()
        return true
    }

    private fun requestFocus(): Boolean {
        return AudioManagerCompat.requestAudioFocus(
            audioManager!!,
            audioFocusRequest
        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d(AUDIO_TAG, "AUDIOFOCUS_GAIN")
                if (!isPlaying && isPausedByTransientLossOfFocus) {
                    start()
                    callbacks?.onPlayStateChanged()
                    isPausedByTransientLossOfFocus = false
                }
                setVolume(Volume.NORMAL)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback and release media playback
                Log.d(AUDIO_TAG, "AUDIOFOCUS_LOSS")
                pause()
                callbacks?.onPlayStateChanged()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media playback because playback
                // is likely to resume
                Log.d(AUDIO_TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                val wasPlaying = isPlaying
                pause()
                callbacks?.onPlayStateChanged()
                isPausedByTransientLossOfFocus = wasPlaying
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                Log.d(AUDIO_TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                setVolume(Volume.DUCK)
            }
        }
    }

    private val audioFocusRequest: AudioFocusRequestCompat =
        AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(audioFocusListener)
            .setAudioAttributes(
                AudioAttributesCompat.Builder()
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC).build()
            ).build()

    object Volume {
        /**
         * The volume we set the media player to when we lose audio focus, but are
         * allowed to reduce the volume instead of stopping playback.
         */
        const val DUCK = 0.2f

        /** The volume we set the media player when we have audio focus.  */
        const val NORMAL = 1.0f
    }
}