package com.jooheon.clean_architecture.presentation.service.music

import android.app.Activity
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.jooheon.clean_architecture.domain.entity.Entity
import java.util.*
import javax.inject.Singleton

object MusicPlayerRemote {
    private val TAG = MusicPlayerRemote::class.java.simpleName
    private val mConnectionMap = WeakHashMap<Context, ServiceBinder>()
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

        val binder = ServiceBinder(callback)
        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MusicService::class.java),
                binder,
                Context.BIND_AUTO_CREATE
            )
        ) {
            mConnectionMap[contextWrapper] = binder
            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap.isEmpty()) {
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

    class ServiceBinder internal constructor(private val mCallback: ServiceConnection?) :
        ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.service
            mCallback?.onServiceConnected(className, service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mCallback?.onServiceDisconnected(className)
            musicService = null
        }
    }
    class ServiceToken internal constructor(internal var mWrappedContext: ContextWrapper)
}