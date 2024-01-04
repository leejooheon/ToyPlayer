package com.jooheon.toyplayer.features.common

import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.guava.asDeferred
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class PlayerController(
    private val context: Context,
    private val sessionToken: SessionToken,
) {
    private var _controller: Deferred<MediaController> = newControllerAsync()

    private fun newControllerAsync() = MediaController
        .Builder(context, sessionToken)
        .buildAsync()
        .asDeferred()

    private val future: Deferred<MediaController>
        get() {
            if (_controller.isCompleted) {
                val completedController = _controller.getCompleted()
                if (!completedController.isConnected) {
                    completedController.release()
                    _controller = newControllerAsync()
                }
            }
            return _controller
        }

    suspend fun connectFuture() {
        Timber.d("connectFuture start")
        awaitConnect()?.let {
            if(it.isConnected) it.release()
        }
        _controller = newControllerAsync()
        Timber.d("connectFuture end")
    }

    suspend fun release() {
        try {
            future.await().also {
                it.release()
            }
            Timber.d("release player")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e("Error while release media controller")
        }
    }

    suspend fun awaitConnect(): MediaController? {
        return try {
            future.await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "Error while connecting to media controller")
            null
        }
    }
}