package com.jooheon.clean_architecture.presentation

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner

import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.view.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    private val TAG = MainActivity::class.simpleName
    @Inject
    lateinit var musicPlayerRemote: MusicPlayerRemote

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
//        musicPlayerRemote.unbindFromService(serviceToken)
        musicPlayerRemote.unsubscribe(MusicService.MEDIA_ID_ROOT)
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme() {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }

    private fun initMusicServiceToken() {
        musicPlayerRemote.subscribe(
            MusicService.MEDIA_ID_ROOT,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    Log.d("MusicService-MainActivity", "onChildrenLoaded - ${children.size}")
                    musicPlayerRemote.updateQueue(children)
                }

                override fun onError(parentId: String) {
                    super.onError(parentId)
                    Log.d("MusicService-MainActivity", "onError")
                }
            }
        )
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