package com.jooheon.clean_architecture.presentation.service.music

import android.app.PendingIntent
import android.content.Intent
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.presentation.service.music.callbacks.MediaPlayerEventListener
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerDataSource
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {
    private val TAG = MusicService::class.java.simpleName

    private val musicBind: IBinder = MusicBinder()

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var musicUseCase: MusicUseCase

    @Inject
    lateinit var dataSourceFactory: DefaultDataSource.Factory

    @Inject
    lateinit var musicDataSource: MusicPlayerDataSource

    private lateinit var playBackPreparer: MusicPlaybackPreparer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var connector: MediaSessionConnector

    private lateinit var mediaPlayerEventListener: MediaPlayerEventListener

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()
        initialize()
        connector.apply {
            setPlayer(exoPlayer)
            setPlaybackPreparer(playBackPreparer)
            setQueueNavigator(QueueNavigator(mediaSession))
        }
        exoPlayer.addListener(mediaPlayerEventListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initialize() {
        Log.d(TAG, "initialize")
        serviceScope.launch {
            musicDataSource.getMusic()
        }

        val activityIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        mediaSession = MediaSessionCompat(this, MEDIA_SESSION).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken


        connector = MediaSessionConnector(mediaSession)
        dataSourceFactory = DefaultDataSource.Factory(this)
        mediaPlayerEventListener = MediaPlayerEventListener(this)

        playBackPreparer = MusicPlaybackPreparer(
            dataSource = musicDataSource,
            onPlayerPrepared = {
                serviceScope.launch {
                    prepareMusic(
                        songToBePlayed = it,
                        allSongs = musicDataSource.allMusicAsMetadata,
                        playNow = true
                    )
                }
            }
        )
    }

    private fun prepareMusic(
        songToBePlayed: MediaMetadataCompat,
        allSongs: List<MediaMetadataCompat>,
        playNow: Boolean
    ) {
        Log.d(TAG, "prepareMusic - ${songToBePlayed.description.title}")
        val index = allSongs.indexOfFirst { it.description.mediaId == songToBePlayed.description.mediaId }
        val songIndex = if(index == -1) { 0 } else { index }
        exoPlayer.prepare(musicDataSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(songIndex, 0L)
        exoPlayer.playWhenReady = playNow
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
        Log.d(TAG, "onGetRoot")
        val browserRootPath = MEDIA_ID_ROOT
        return BrowserRoot(browserRootPath, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>,
    ) {
        Log.d(TAG, "onLoadChildren")
        val uri = MusicUtil.localMusicStorageUri().toString()
        musicUseCase.getSongs(uri).onEach { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.value.map { MusicUtil.parseMediaItemFromSong(it) }.also {
                        result.sendResult(it)
                    }
                }
                is Resource.Failure -> result.sendError(null)
                else -> { /** Nothing **/ }
            }
        }.launchIn(serviceScope)
        result.detach()
    }
    fun openQueue(playingQueue: List<Entity.Song>?) { // 플레이리스트를 업데이트한다.
        if(playingQueue == null) return
    }

    private fun runOnUiThread(content: () -> Unit) {
        serviceScope.launch(Dispatchers.Main) {
            content.invoke()
        }
    }

    companion object {
        const val MEDIA_ID_ROOT = "__ROOT__"
        private const val MEDIA_SESSION = "JooheonMediaSession"


        const val DURATION = "duration"
        const val TRACK_NUMBER = "track_number"
        const val YEAR = "year"
        const val DATA = "data"
        const val DATE_MODIFIED = "date_modified"
        const val ALBUM_ID = "album_id"
        const val ALBUM_NAME = "album_name"
        const val ALBUM_ARTIST = "album_artist"
        const val ARTIST_ID = "artist_id"
        const val ARTIST_NAME = "artist_name"
        const val COMPOSER = "composer"
    }


    private inner class QueueNavigator(mediaSessionCompat: MediaSessionCompat) :
        TimelineQueueNavigator(mediaSessionCompat) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicDataSource.allMusicAsMetadata[windowIndex].description
        }
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }
}