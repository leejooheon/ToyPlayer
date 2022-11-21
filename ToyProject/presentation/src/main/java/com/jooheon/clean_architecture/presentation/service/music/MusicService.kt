package com.jooheon.clean_architecture.presentation.service.music

import android.content.Intent
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.common.showToast
import com.jooheon.clean_architecture.presentation.service.music.playback.Playback
import com.jooheon.clean_architecture.presentation.service.music.playback.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList

class MusicService: MediaBrowserServiceCompat() {
    private val TAG = MusicService::class.java.simpleName

    private val musicBind: IBinder = MusicBinder()
    private lateinit var storage: PersistentStorage

    private lateinit var playbackManager: PlaybackManager
    val playback: Playback? get() = playbackManager.playback

    private var uiThreadHandler: Handler? = null

    val currentSong: Entity.Song
        get() = getSongAt(getPosition())

    @JvmField
    var position = -1
    @JvmField
    var playingQueue = ArrayList<Entity.Song>()
    private var originalPlayingQueue = ArrayList<Entity.Song>()

    private val serviceScope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        playbackManager = PlaybackManager(this)
        uiThreadHandler = Handler(Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        // For Android auto, need to call super, or onGetRoot won't be called.
        return if ("android.media.browse.MediaBrowserService" == intent.action) {
            super.onBind(intent)!!
        } else musicBind
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot {
        val browserRootPath = MEDIA_ID_ROOT
        return BrowserRoot(browserRootPath, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>,
    ) {
        result.sendResult(listOf(storage.recentSong()))
    }


    fun openQueue(
        playingQueue: List<Entity.Song>?
    ) {
        if(playingQueue == null) return

        Log.d(TAG, "openQueue ${playingQueue.first()}")
        originalPlayingQueue = ArrayList(playingQueue)
        this.playingQueue = ArrayList(playingQueue)

        playSongAt(0)
    }

    fun playSongAt(position: Int) {
        // Every chromecast method needs to run on main thread or you are greeted with IllegalStateException
        // Playback Manager 추가하자.
        serviceScope.launch(Dispatchers.Default) {
            openTrackAndPrepareNextAt(position) { success ->
                if (success) {
                    play()
                } else {
                    runOnUiThread {
                        showToast(getString(R.string.some_error))
                    }
                }
            }
        }
    }

    @Synchronized
    fun openTrackAndPrepareNextAt(position: Int, completion: (success: Boolean) -> Unit) {
        this.position = position
        openCurrent { success ->
            Log.d(TAG, "is Success: ${success}")
            completion(success)
        }
    }
    @Synchronized
    private fun openCurrent(completion: (success: Boolean) -> Unit) {
        val force = true

        playbackManager.setDataSource(currentSong, force) { success ->
            completion(success)
        }
    }

    @Synchronized
    fun play() {
        playbackManager.play { playSongAt(getPosition()) }
//        if (notHandledMetaChangedForCurrentTrack) {
//            handleChangeInternal(META_CHANGED)
//            notHandledMetaChangedForCurrentTrack = false
//        }
//        notifyChange(PLAY_STATE_CHANGED)
    }

    private fun getSongAt(position: Int): Entity.Song {
        return if ((position >= 0) && (position < playingQueue.size)) {
            playingQueue[position]
        } else {
            Entity.Song.emptySong
        }
    }
    private fun getPosition(): Int {
        return position
    }

    fun runOnUiThread(runnable: Runnable?) {
        uiThreadHandler?.post(runnable!!)
    }

    companion object {
        const val MEDIA_ID_ROOT = "__ROOT__"
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }
}