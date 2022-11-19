package com.jooheon.clean_architecture.presentation.service.music

import android.util.Log
import com.jooheon.clean_architecture.domain.entity.Entity
import javax.inject.Singleton

@Singleton
class MusicPlayerRemote {
    private val TAG = MusicPlayerRemote::class.java.simpleName
    var musicService: MusicService? = null
    /**
     * Async
     */
    fun openQueue(queue: List<Entity.Song>) {
        Log.d(TAG, "openQueue: ${queue.first()}")
//        musicService?.openQueue(queue, startPosition, startPlaying)
    }
}