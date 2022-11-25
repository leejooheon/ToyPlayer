package com.jooheon.clean_architecture.presentation.service.music

import android.app.Activity
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.jooheon.clean_architecture.domain.entity.Entity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerRemote @Inject constructor(@ApplicationContext private val context: Context) {
    private val TAG = MusicPlayerRemote::class.java.simpleName
    private val connectionMap = WeakHashMap<Context, ServiceBinder>()
    private var musicService: MusicService? = null

    fun bindToService(context: Context, callback: ServiceConnection): ServiceToken? {
        val realActivity = (context as Activity).parent ?: context
        val contextWrapper = ContextWrapper(realActivity)
        val intent = Intent(contextWrapper, MusicService::class.java)
        try {
            contextWrapper.startService(intent)
        } catch (ignored: IllegalStateException) {
            runCatching {
                ContextCompat.startForegroundService(context, intent)
            }
        }

        val binder = ServiceBinder(
            serviceConnected = { className, service ->
                val binder = service as MusicService.MusicBinder
                musicService = binder.service
                callback.onServiceConnected(className, service)
            },
            serviceDisconnected = { className ->
                callback.onServiceDisconnected(className)
                musicService = null
            }
        )
        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MusicService::class.java),
                binder,
                Context.BIND_AUTO_CREATE
            )
        ) {
            connectionMap[contextWrapper] = binder
            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = connectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (connectionMap.isEmpty()) {
            musicService = null
        }
    }

    /**
     * Async
     */
    fun openQueue(queue: List<Entity.Song>) {
        Log.d(TAG, "openQueue: ${queue.first()}")
        musicService?.openQueue(queue)
    }

    fun pauseSong() {
        musicService?.pause()
    }

    class ServiceBinder internal constructor(
        private val serviceConnected: (className: ComponentName, service: IBinder) -> Unit,
        private val serviceDisconnected: (className: ComponentName) -> Unit
    ) : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            serviceConnected(className, service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            serviceDisconnected(className)
        }
    }
    class ServiceToken internal constructor(internal var mWrappedContext: ContextWrapper)
}