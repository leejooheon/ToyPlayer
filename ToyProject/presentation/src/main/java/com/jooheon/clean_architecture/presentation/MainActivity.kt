package com.jooheon.clean_architecture.presentation

import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner

import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.service.music.IMusicServiceEventListener
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.FAVORITE_STATE_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.MEDIA_STORE_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.META_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.PLAY_STATE_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.QUEUE_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.REPEAT_MODE_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.SHUFFLE_MODE_CHANGED
import com.jooheon.clean_architecture.presentation.service.music.MusicStateReceiver
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    private val TAG = MainActivity::class.simpleName

    private var serviceToken: MusicPlayerRemote.ServiceToken? = null
    private var receiverRegistered: Boolean = false
    private var musicStateReceiver: MusicStateReceiver? = null
    private val musicServiceEventListeners = ArrayList<IMusicServiceEventListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMusicService()

        setContent {
            AppContent()
        }

        setOwners()
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme() {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }

    private fun initMusicService() {
        serviceToken = MusicPlayerRemote.bindToService(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                this@MainActivity.onServiceConnected()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                this@MainActivity.onServiceDisconnected()
            }
        })
    }

    private fun onServiceConnected() {
        Log.d(TAG, "onServiceConnected")
        if (!receiverRegistered) {
            musicStateReceiver = MusicStateReceiver(this)

            val filter = IntentFilter()
            filter.addAction(PLAY_STATE_CHANGED)
            filter.addAction(SHUFFLE_MODE_CHANGED)
            filter.addAction(REPEAT_MODE_CHANGED)
            filter.addAction(META_CHANGED)
            filter.addAction(QUEUE_CHANGED)
            filter.addAction(MEDIA_STORE_CHANGED)
            filter.addAction(FAVORITE_STATE_CHANGED)

            registerReceiver(musicStateReceiver, filter)

            receiverRegistered = true
        }

        for (listener in musicServiceEventListeners) {
            listener.onServiceConnected()
        }
    }

    private fun onServiceDisconnected() {
        Log.d(TAG, "onServiceDisconnected")
        if (receiverRegistered) {
            unregisterReceiver(musicStateReceiver)
            receiverRegistered = false
        }

        for (listener in musicServiceEventListeners) {
            listener.onServiceDisconnected()
        }
    }

}

private fun ComponentActivity.setOwners() { // TODO: 이게 뭐지??
    val decorView = window.decorView
    if (ViewTreeLifecycleOwner.get(decorView) == null) {
        ViewTreeLifecycleOwner.set(decorView, this)
    }
    if (ViewTreeViewModelStoreOwner.get(decorView) == null) {
        ViewTreeViewModelStoreOwner.set(decorView, this)
    }
}
/*
suspend fun fcmRegister() {
    val fcmToken = try {
        suspendCoroutine<String> {
            firebaseMessage.token
                .addOnSuccessListener { fcmToken ->
                    it.resume(fcmToken)
                }
                .addOnFailureListener { t ->
                    it.resumeWithException(t)
                }
        }
    } catch (t: Throwable) {
        firebaseCrashlytics.recordException(t)
        return
    }

    restApiService.register(fcmToken)
}
 */