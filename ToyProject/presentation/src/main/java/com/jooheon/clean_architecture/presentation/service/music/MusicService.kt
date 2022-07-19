package com.jooheon.clean_architecture.presentation.service.music

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.getSystemService
import androidx.media.MediaBrowserServiceCompat
import com.jooheon.clean_architecture.presentation.BuildConfig
import com.jooheon.clean_architecture.presentation.service.music.notification.PlayingNotification
import com.jooheon.clean_architecture.presentation.service.music.notification.PlayingNotificationImpl
import com.jooheon.clean_architecture.presentation.utils.VersionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MusicService: MediaBrowserServiceCompat() {
    private val serviceScope = CoroutineScope(Job() + Dispatchers.Main)
    private var mediaSession: MediaSessionCompat? = null
    private var playingNotification: PlayingNotification? = null // Notification
    private var notificationManager: NotificationManager? = null // 이것을 통해 playingNotification의 새로고침/생성을 관리함
    private var isForeground = false
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()

        setupMediaSession()
        initNotification()
        startForeground()
    }

    private fun setupMediaSession() {
        val mediaSessionCallback = MediaSessionCallback(this)

        val mediaButtonReceiverComponentName = ComponentName(
            applicationContext,
            MediaButtonIntentReceiver::class.java // Notification 버튼 리시버, 마지막엔 onStartCommand에서 받는다.
        )

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.component = mediaButtonReceiverComponentName
        val mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast( // PendingIntent가 있어야 서비스와 통신가능함.
            applicationContext, 0, mediaButtonIntent,
            if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE else 0
        )

        mediaSession = MediaSessionCompat( // 미디어세션 만들고
            this,
            BuildConfig.APPLICATION_ID,
            mediaButtonReceiverComponentName,
            mediaButtonReceiverPendingIntent
        ).apply {
            setCallback(mediaSessionCallback) // 콜백 달아주고 (아직 미구현)
            setMediaButtonReceiver(mediaButtonReceiverPendingIntent) // 버튼 리시버도 달아준다.
            isActive = true
        }
    }

    private fun initNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }
        notificationManager = getSystemService()
        playingNotification = PlayingNotificationImpl.from(this, notificationManager!!, mediaSession!!)
        // 커스텀 NotificationCompat.Builder를 만들어서, 버튼같은거 내맘대로 세팅
    }

    private fun startForeground() {
        playingNotification?.let { playingNotification ->
            if (isForeground && !isPlaying) {
                // This makes the notification dismissible
                // We can't call stopForeground(false) on A12 though, which may result in crashes
                // when we call startForeground after that e.g. when Alarm goes off,
                if (!VersionUtils.hasS()) {
                    stopForeground(false)
                    isForeground = false
                }
            }
            if (!isForeground && !isPlaying) {
                // Specify that this is a media service, if supported.
                if (VersionUtils.hasQ()) {
                    startForeground(
                        PlayingNotification.NOTIFICATION_ID, playingNotification.build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(
                        PlayingNotification.NOTIFICATION_ID,
                        playingNotification.build()
                    )
                }
                isForeground = true
                isPlaying = true
            } else {
                notifyToNotificationManager()
            }
        }
    }

    private fun notifyToNotificationManager() {
        playingNotification?.let { playingNotification ->
            notificationManager?.notify(PlayingNotification.NOTIFICATION_ID, playingNotification.build())
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot(MEDIA_ID_ROOT, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot(MEDIA_ID_EMPTY_ROOT, null)
        }
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
//        if (parentId == AutoMediaIDHelper.RECENT_ROOT) {
//            result.sendResult(listOf(storage.recentSong()))
//        } else {
//            result.sendResult(mMusicProvider.getChildren(parentId, resources))
//        }

        if (MEDIA_ID_EMPTY_ROOT == parentMediaId) {
            result.sendResult(null)
            return
        }

        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>().toMutableList()

        // Check if this is the root menu:
        if (MEDIA_ID_ROOT == parentMediaId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems)
    }

    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        Log.d(TAG, "packageName: $clientPackageName, uid: $clientUid")
        return true // FIXME
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            serviceScope.launch {
                when (intent.action) {
                    ACTION_TOGGLE_PAUSE -> {
                        Log.d(TAG, "ACTION_TOGGLE_PAUSE")
                        isPlaying = !isPlaying
                        playingNotification?.setPlaying(isPlaying)
                        notifyToNotificationManager()
                    }
                    ACTION_PAUSE -> Log.d(TAG, "ACTION_PAUSE")
                    ACTION_PLAY -> Log.d(TAG, "ACTION_PLAY")
                    ACTION_PLAY_PLAYLIST -> Log.d(TAG, "ACTION_PLAY_PLAYLIST")
                    ACTION_REWIND -> Log.d(TAG, "ACTION_REWIND")
                    ACTION_SKIP -> Log.d(TAG, "ACTION_SKIP")
                    ACTION_STOP, ACTION_QUIT -> Log.d(TAG, "ACTION_STOP, ACTION_QUIT")
                    ACTION_PENDING_QUIT -> Log.d(TAG, "ACTION_PENDING_QUIT")
                    TOGGLE_FAVORITE -> Log.d(TAG, "TOGGLE_FAVORITE")
                }
            }
        }
        return START_NOT_STICKY
    }

    companion object {
        val TAG: String = MusicService::class.java.simpleName
        const val MY_MUSIC_PACKAGE_NAME = "jooheon.clean_architecture.toyproject.music"
        const val MUSIC_PACKAGE_NAME = "com.android.music"
        const val ACTION_TOGGLE_PAUSE = "$MY_MUSIC_PACKAGE_NAME.togglepause"
        const val ACTION_PLAY = "$MY_MUSIC_PACKAGE_NAME.play"
        const val ACTION_PLAY_PLAYLIST = "$MY_MUSIC_PACKAGE_NAME.play.playlist"
        const val ACTION_PAUSE = "$MY_MUSIC_PACKAGE_NAME.pause"
        const val ACTION_STOP = "$MY_MUSIC_PACKAGE_NAME.stop"
        const val ACTION_SKIP = "$MY_MUSIC_PACKAGE_NAME.skip"
        const val ACTION_REWIND = "$MY_MUSIC_PACKAGE_NAME.rewind"
        const val ACTION_QUIT = "$MY_MUSIC_PACKAGE_NAME.quitservice"
        const val ACTION_PENDING_QUIT = "$MY_MUSIC_PACKAGE_NAME.pendingquitservice"

        const val MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__"
        const val MEDIA_ID_ROOT = "__ROOT__"

        // Do not change these three strings as it will break support with other apps (e.g. last.fm
        // scrobbling)
        const val META_CHANGED = "$MY_MUSIC_PACKAGE_NAME.metachanged"
        const val QUEUE_CHANGED = "$MY_MUSIC_PACKAGE_NAME.queuechanged"
        const val PLAY_STATE_CHANGED = "$MY_MUSIC_PACKAGE_NAME.playstatechanged"
        const val FAVORITE_STATE_CHANGED = "$MY_MUSIC_PACKAGE_NAME.favoritestatechanged"
        const val REPEAT_MODE_CHANGED = "$MY_MUSIC_PACKAGE_NAME.repeatmodechanged"
        const val SHUFFLE_MODE_CHANGED = "$MY_MUSIC_PACKAGE_NAME.shufflemodechanged"
        const val MEDIA_STORE_CHANGED = "$MY_MUSIC_PACKAGE_NAME.mediastorechanged"
        const val CYCLE_REPEAT = "$MY_MUSIC_PACKAGE_NAME.cyclerepeat"
        const val TOGGLE_SHUFFLE = "$MY_MUSIC_PACKAGE_NAME.toggleshuffle"
        const val TOGGLE_FAVORITE = "$MY_MUSIC_PACKAGE_NAME.togglefavorite"
    }
}