package com.jooheon.clean_architecture.presentation

import android.content.ComponentName
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
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseComposeActivity(), IMusicServiceEventListener {
    private val TAG = MainActivity::class.simpleName
    private var serviceToken: MusicPlayerRemote.ServiceToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMusicServiceToken()
        setContent {
            AppContent()
        }

        setOwners()
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerRemote.unbindFromService(serviceToken)
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme() {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }

    private fun initMusicServiceToken() {
        serviceToken = MusicPlayerRemote.bindToService(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                this@MainActivity.onServiceConnected()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                this@MainActivity.onServiceDisconnected()
            }
        })
    }

    override fun onServiceConnected() {
        Log.d(TAG, "onServiceConnected")
    }

    override fun onServiceDisconnected() {
        Log.d(TAG, "onServiceDisconnected")
    }

    override fun onQueueChanged() {
        Log.d(TAG, "onQueueChanged")
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