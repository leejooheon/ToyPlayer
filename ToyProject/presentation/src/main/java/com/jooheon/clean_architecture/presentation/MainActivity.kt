package com.jooheon.clean_architecture.presentation

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.jooheon.clean_architecture.domain.common.Resource

import com.jooheon.clean_architecture.presentation.base.BaseComposeActivity
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.theme.themes.ApplicationTheme
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.view.NavGraphs
import com.jooheon.clean_architecture.presentation.view.main.MusicPlayerViewModel
import com.ramcosta.composedestinations.DestinationsNavHost

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseComposeActivity() {
    private val TAG = MainActivity::class.simpleName

    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMusicServiceToken()
        setContent {
            AppContent()
        }
    }

    @Composable
    private fun AppContent() {
        ApplicationTheme() {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }

    private fun initMusicServiceToken() {
        lifecycleScope.launch {
            val resource = musicPlayerViewModel.subscribeToService()
            if(resource is Resource.Success) {
                val allSongs = resource.value
                Log.d("MusicService-MainActivity", "onChildrenLoaded - ${allSongs.size}")
                allSongs.map { MusicUtil.parseSongFromMediaItem(it) }.also {
                    musicPlayerViewModel.updateSongList(it)
                }
            } else {
                Log.d("MusicService-MainActivity", (resource as? Resource.Failure)?.message ?: "Failure")
            }
        }
    }
}