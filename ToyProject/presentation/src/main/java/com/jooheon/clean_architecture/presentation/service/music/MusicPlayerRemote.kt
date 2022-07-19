package com.jooheon.clean_architecture.presentation.service.music

import android.app.Activity
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import android.provider.DocumentsContract
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.set


object MusicPlayerRemote {
    val TAG: String = MusicPlayerRemote::class.java.simpleName

    private val mConnectionMap = WeakHashMap<Context, ServiceBinder>()

    var musicService: MusicService? = null
        private set

    val isServiceConnected: Boolean
        get() = musicService != null

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

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }
//
//    @JvmStatic
//    fun playFromUri(context: Context, uri: Uri) {
//        if (musicService != null) {
//
//            var songs: List<Song>? = null
//            if (uri.scheme != null && uri.authority != null) {
//                if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
//                    var songId: String? = null
//                    if (uri.authority == "com.android.providers.media.documents") {
//                        songId = getSongIdFromMediaProvider(uri)
//                    } else if (uri.authority == "media") {
//                        songId = uri.lastPathSegment
//                    }
//                    if (songId != null) {
//                        songs = songRepository.songs(songId)
//                    }
//                }
//            }
//            if (songs == null || songs.isEmpty()) {
//                var songFile: File? = null
//                if (uri.authority != null && uri.authority == "com.android.externalstorage.documents") {
//                    val path = uri.path?.split(":".toRegex(), 2)?.get(1)
//                    if (path != null) {
//                        songFile = File(getExternalStorageDirectory(), path)
//                    }
//                }
//                if (songFile == null) {
//                    val path = getFilePathFromUri(context, uri)
//                    if (path != null)
//                        songFile = File(path)
//                }
//                if (songFile == null && uri.path != null) {
//                    songFile = File(uri.path!!)
//                }
//                if (songFile != null) {
//                    songs = songRepository.songsByFilePath(songFile.absolutePath, true)
//                }
//            }
//            if (songs != null && songs.isNotEmpty()) {
//                openQueue(songs, 0, true)
//            } else {
//                try {
//                    context.showToast(R.string.unplayable_file)
//                } catch (e: Exception) {
//                    logE("The file is not listed in the media store")
//                }
//            }
//        }
//    }

    private fun getSongIdFromMediaProvider(uri: Uri): String {
        return DocumentsContract.getDocumentId(uri).split(":".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()[1]
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
